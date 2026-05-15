package com.zendr.backend.services.event;


import com.mongodb.client.model.geojson.GeoJsonObjectType;
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
import com.zendr.backend.services.geocoding.GeocodingService;
import com.zendr.backend.services.storage.BucketService;
import com.zendr.backend.services.weather.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final GeocodingService geocodingService;
    private final WeatherService weatherService;
    private final BucketService bucketService;
    
    @Transactional
    public EventResponse save(CreateEventRequest request) {
        
        try {
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
            
            // SUBIR IMAGEN
            String eventImgUrl = bucketService.uploadFile(request.eventImgUrl(), "events");
            
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
                    saved.getEventImgUrl(),
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
        } catch (IOException e) {
            throw new RuntimeException("Error al subir la imagen del envento");
        }
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
                            event.getEventImgUrl(),
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