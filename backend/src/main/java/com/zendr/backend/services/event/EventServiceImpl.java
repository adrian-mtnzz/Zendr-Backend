package com.zendr.backend.services.event;


import com.mongodb.client.model.geojson.GeoJsonObjectType;
import com.zendr.backend.internal.booking.model.Booking;
import com.zendr.backend.internal.booking.repository.BookingRepository;
import com.zendr.backend.internal.discipline.model.Discipline;
import com.zendr.backend.internal.discipline.repository.DisciplineRepository;
import com.zendr.backend.internal.event.dtos.*;
import com.zendr.backend.internal.event.model.Event;
import com.zendr.backend.internal.event.model.EventLocation;
import com.zendr.backend.internal.event.repository.EventRepository;
import com.zendr.backend.internal.user.model.FavDisciplines;
import com.zendr.backend.internal.user.model.User;
import com.zendr.backend.internal.user.model.enums.UserRole;
import com.zendr.backend.internal.user.repository.UserRepository;
import com.zendr.backend.internal.waitList.model.WaitList;
import com.zendr.backend.internal.waitList.repository.WaitListRepository;
import com.zendr.backend.internal.weather.model.Weather;
import com.zendr.backend.internal.weather.repository.WeatherRepository;
import com.zendr.backend.services.booking.BookingService;
import com.zendr.backend.services.geocoding.GeocodingService;
import com.zendr.backend.services.storage.BucketService;
import com.zendr.backend.services.weather.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    
    private final EventRepository repository;
    private final DisciplineRepository disciplineRepository;
    private final UserRepository userRepository;
    private final WeatherRepository weatherRepository;
    private final WaitListRepository waitListRepository;
    private final BookingRepository bookingRepository;
    private final GeocodingService geocodingService;
    private final WeatherService weatherService;
    private final BucketService bucketService;
    
    
    @Transactional
    public EventResponse save(CreateEventRequest request, MultipartFile file) {
        
        // VALIDACIONES INICIALES
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        if (user.getRole().equals(UserRole.USER)) {
            throw new IllegalArgumentException(
                    "La creación de eventos no está permitida para este tipo de usuario, debe ser Monitor o superior");
        }
        
        Discipline discipline = disciplineRepository.findById(request.disciplineId())
                .orElseThrow(() -> new IllegalArgumentException("Disciplina no encontrada"));
        
        
        // OBTENER COORDENADAS
        EventLocation location = EventLocation.builder()
                .coordsType(GeoJsonObjectType.POINT)
                .coords(geocodingService.getCoordinates(
                        request.address(),
                        request.city(),
                        request.region(),
                        request.country()))
                .build();
        
        // CREAR WEATHER
        Weather weather = weatherService.getCurrentWeather(
                location.getCoords().longitud(),
                location.getCoords().latitud()
        );
        
        Weather savedWeather = weatherRepository.save(weather);
        
        // CREAR WAITLIST
        WaitList waitList = waitListRepository.save(new WaitList());
        
        String eventImgUrl = null;
        try {
            // SUBIR IMAGEN
            if (file != null) {
                eventImgUrl = Objects.requireNonNull(file.getContentType()).startsWith("image/")
                        ? bucketService.uploadFile(file, "events")
                        : "events/b4301c46-9318-4820-a491-f98eebda7f8b.jpeg"; // Fallback imagen para eventos
            } else eventImgUrl = "events/b4301c46-9318-4820-a491-f98eebda7f8b.jpeg";
        
        } catch (IOException e) {
        
        } finally {
            eventImgUrl = "events/b4301c46-9318-4820-a491-f98eebda7f8b.jpeg";
        }
        
        // CREAR EVENTO
        Event event = Event.builder()
                .name(request.name())
                .eventImgUrl(eventImgUrl)
                .placeCommonName(request.placeCommonName())
                .address(request.address())
                .city(request.city())
                .region(request.region())
                .countryCode(request.country())
                .zip(request.zip())
                .description(request.description())
                .monitorId(user.getId())
                .disciplineId(discipline.getId())
                .level(request.level())
                .weatherId(savedWeather.getId())
                .waitListId(waitList.getId())
                .startsAt(request.startsAt())
                .duration(request.duration())
                .location(location)
                .priceDetails(request.priceDetails())
                .capacity(request.capacity())
                .build();
        
        Event saved = repository.save(event);
        
        
        // RESPONSE
        return new EventResponse(
                saved.getId(),
                bucketService.generatePresignedUrl(saved.getEventImgUrl()),
                saved.getName(),
                saved.getPlaceCommonName(),
                saved.getAddress(),
                saved.getCity(),
                saved.getRegion(),
                saved.getCountryCode(),
                saved.getZip(),
                saved.getDescription(),
                saved.getMonitorId(),
                saved.getDisciplineId(),
                saved.getLevel().name(),
                saved.getWaitListId(),
                saved.getStartsAt(),
                saved.getDuration(),
                saved.getEndsAt(),
                savedWeather,
                saved.getLocation(),
                saved.getPriceDetails(),
                saved.getCapacity(),
                Event.EventStatus.ACTIVE.getDescription()
        );
    }
    
    @Transactional
    @PreAuthorize("@eventRepository.findById(#eventId).get().monitor.id == authentication.principal.id")
    public boolean cancelEvent(String eventId) {
        
        Event event = repository.findById(eventId)
                .orElseThrow();
        
        event.setStatus(Event.EventStatus.CANCELLED);
        bookingRepository.findByEventId(eventId).forEach(
                booking -> {
                    booking.setStatus(Booking.BookingStatus.CANCELED);
                }
        );
        repository.save(event);
        
        return true;
    }
    
    public EventDetailsResponse getEventDetails(String eventId) {
        
        Event event = repository.findById(eventId).orElseThrow(
                () -> new IllegalArgumentException("Evento no encontrado")
        );
        
        EventLocation.Coordinates coords = event.getLocation().getCoords();
        Weather oldWeather = weatherRepository.findById(event.getWeatherId()).orElseThrow(
                ()-> new IllegalArgumentException("No se ha encontrado el tiempo para este evento")
        );
        
        Weather newWeather = weatherService.getCurrentWeather(coords.longitud(), coords.latitud());
        
        oldWeather.setTemperatureInCelsius(newWeather.getTemperatureInCelsius());
        oldWeather.setCondition(newWeather.getCondition());
        oldWeather.setDescription(newWeather.getDescription());
        oldWeather.setIconUrl(newWeather.getIconUrl());
        oldWeather.setAptOutdoors(newWeather.isAptOutdoors());
        oldWeather.setLastUpdate(Instant.now());
        
        Weather savedWeather = weatherRepository.save(oldWeather);
        
        User monitor = userRepository.findById(event.getMonitorId()).orElseThrow(
                () -> new IllegalArgumentException("No se ha encontrado al monitor")
        );
        
        return EventDetailsResponse.builder()
                .id(event.getId())
                .eventImgUrl(bucketService.generatePresignedUrl(event.getEventImgUrl()))
                .name(event.getName())
                .placeCommonName(event.getPlaceCommonName())
                .address(event.getAddress())
                .city(event.getCity())
                .region(event.getRegion())
                .countryCode(event.getCountryCode())
                .zip(event.getZip())
                .description(event.getDescription())
                .monitorId(event.getMonitorId())
                .monitorProfileImg(bucketService.generatePresignedUrl(monitor.getProfileImg()))
                .monitorName(monitor.getName())
                .disciplineId(event.getDisciplineId())
                .level(event.getLevel().getDescription())
                .waitListId(event.getWaitListId())
                .startsAt(event.getStartsAt())
                .duration(event.getDuration())
                .weather(savedWeather)
                .location(event.getLocation())
                .priceDetails(event.getPriceDetails())
                .capacity(event.getCapacity())
                .status(event.getStatus().getDescription())
                .build();
    }
    
    public Page<SearchEventDTO> filterAndOrderAllEvents(
            SearchEventsRequest request,
            Pageable pageable
    ) {
        
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        double[] coords = request.coords();
        
        List<Event> events = repository.findAll();
        
        events = applyFilters(user, events, request.filters());
        events = applyOrdering(events, request.order(), coords);
        
        List<SearchEventDTO> dtoList = events.stream()
                .map(event -> {
                    Weather weather = weatherRepository.findById(event.getWeatherId())
                            .orElseThrow(() -> new IllegalArgumentException("Condiciones meteorológicas no encontradas"));
                    
                    Discipline discipline = disciplineRepository
                            .findById(event.getDisciplineId())
                            .orElseThrow(() -> new IllegalArgumentException("Disciplina no encontrada"));
                    
                    return new SearchEventDTO(
                            weather.getTemperatureInCelsius(),
                            weather.getIconUrl(),
                            distance(
                                    coords[0],
                                    coords[1],
                                    event.getLocation().getCoords().latitud(),
                                    event.getLocation().getCoords().longitud()
                            ),
                            bucketService.generatePresignedUrl(event.getEventImgUrl()),
                            event.getName(),
                            event.getPlaceCommonName(),
                            discipline.getName(),
                            event.getLevel().getDescription(),
                            event.getStartsAt(),
                            event.getPriceDetails().getPrice(),
                            event.getPriceDetails().getCurrency().getSymbol()
                    );
                })
                .toList();
        
        int start = (int) pageable.getOffset();
        
        if (start >= dtoList.size()) {
            return new PageImpl<>(List.of(), pageable, dtoList.size());
        }
        
        int end = Math.min(start + pageable.getPageSize(), dtoList.size());
        
        List<SearchEventDTO> pageContent = dtoList.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }
    
    
    
    
    
    // =======================
    //        FILTRADO
    // =======================
    
    private List<Event> applyFilters(
            User user,
            List<Event> events,
            SearchFilters filters
    ) {
        
        Set<String> disciplineIds = resolveDisciplineIds(user, filters);
        
        return events.stream()
                
                // EVENTOS ACTIVOS
                .filter(e -> e.getStartsAt().isAfter(Instant.now()))
                
                // PRECIO (<=)
                .filter(e ->
                        filters == null ||
                                filters.price() == null ||
                                e.getPriceDetails().getPrice().compareTo(filters.price()) <= 0
                )
                
                // FECHA
                .filter(e -> {
                    if (filters.isBefore() == null) return true;
                    
                    return e.getStartsAt().isBefore(filters.isBefore());
                })
                
                // SEARCH flexible
                .filter(e -> {
                    if (filters == null || filters.search() == null || filters.search().isBlank()) {
                        return true;
                    }
                    
                    if (e.getSearch() == null) {
                        return false;
                    }
                    
                    String eventText = normalize(e.getSearch());
                    String searchText = normalize(filters.search());
                    
                    return Arrays.stream(searchText.split("\\s+"))
                            .allMatch(eventText::contains);
                })
                
                // DISCIPLINAS
                .filter(e ->
                        disciplineIds.isEmpty() ||
                                disciplineIds.contains(e.getDisciplineId())
                )
                
                // LEVELS
                .filter(e -> {
                    if (filters == null ||
                            filters.levels() == null ||
                            filters.levels().isEmpty()) {
                        return true;
                    }
                    
                    return filters.levels().contains(e.getLevel().name());
                })
                
                .toList();
    }
    
    
    private Set<String> resolveDisciplineIds(User user, SearchFilters filters) {
        
        if (filters == null) {
            return Set.of();
        }
        
        if (filters.disciplinesNames() != null && !filters.disciplinesNames().isEmpty()) {
            return filters.disciplinesNames().stream()
                    .map(name -> disciplineRepository.findByName(name)
                            .map(Discipline::getId)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
        
        if (user.getDeportiveProfile() != null &&
                user.getDeportiveProfile().getFavDisciplines() != null) {
            
            return user.getDeportiveProfile()
                    .getFavDisciplines()
                    .stream()
                    .map(FavDisciplines::getDisciplineId)
                    .filter(disciplineRepository::existsById)
                    .collect(Collectors.toSet());
        }
        
        return Set.of();
    }
    
    
    
    
    private static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
    
    
    
    
    // ==============================
    //          ORDENACION
    // ==============================
    
    private List<Event> applyOrdering(
            List<Event> events,
            SearchOrderCriteria order,
            double[] coords
    ) {
        if (order == null) {
            return orderByProximity(events, coords);
        }
        
        // FECHA
        if(order.isTime()) {
           return orderByClosestDate(events);
        }
        
        // PRECIO
        if (order.isPrice()) {
            return orderByPrice(events);
        }
        
        // NIVEL
        if (order.isLevel()) {
            return orderByLevel(events);
        }
        
        // PROXIMIDAD POR DEFECTO
        return orderByProximity(events, coords);
    }
    
    
    private List<Event> orderByClosestDate(List<Event> events) {
        
        Instant now = Instant.now();
        
        return events.stream()
                .sorted(Comparator.comparing(
                        e -> Duration.between(
                                now,
                                e.getStartsAt()
                        ).abs()
                ))
                .toList();
    }
    
    
    private List<Event> orderByPrice(List<Event> events) {
        
        return events.stream()
                .sorted(Comparator.comparing(
                        e -> e.getPriceDetails().getPrice()
                ))
                .toList();
    }
    
    
    private List<Event> orderByLevel(List<Event> events) {
        
        return events.stream()
                .sorted(Comparator.comparingInt(
                        e -> e.getLevel() != null
                                ? e.getLevel().ordinal()
                                : Integer.MAX_VALUE
                ))
                .toList();
    }
    
    
    private List<Event> orderByProximity(
            List<Event> events,
            double[] coords
    ) {
        
        if (coords == null || coords.length != 3) {
            return events;
        }
        
        double lon = coords[0];
        double lat = coords[1];
        
        return events.stream()
                .sorted(Comparator.comparingDouble(
                        e -> distance(
                                lon,
                                lat,
                                e.getLocation().getCoords().longitud(),
                                e.getLocation().getCoords().latitud()
                        )
                ))
                .toList();
    }
    
    // Formula de Haversine
    private double distance(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        double R = 6371000; // km
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(lat1)) *
                                Math.cos(Math.toRadians(lat2)) *
                                Math.sin(dLon / 2) *
                                Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}