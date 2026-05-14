package com.zendr.backend.services.event;


import com.zendr.backend.internal.discipline.model.Discipline;
import com.zendr.backend.internal.discipline.repository.DisciplineRepository;
import com.zendr.backend.internal.event.dtos.EventsResponse;
import com.zendr.backend.internal.event.dtos.EventsSearchRequest;
import com.zendr.backend.internal.event.dtos.SearchFilters;
import com.zendr.backend.internal.event.dtos.SearchOrderCriteria;
import com.zendr.backend.internal.event.model.Event;
import com.zendr.backend.internal.event.repository.EventRepository;
import com.zendr.backend.internal.user.model.FavDisciplines;
import com.zendr.backend.internal.user.model.User;
import com.zendr.backend.internal.user.model.enums.FavDisciplinesCurrentLevel;
import com.zendr.backend.internal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.awt.geom.Point2D.distance;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    
    private final EventRepository repository;
    private final DisciplineRepository disciplineRepository;
    private final UserRepository userRepository;
    
    
    public EventsResponse filterAndOrderAllEvents(EventsSearchRequest request) {
        
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Event> events = repository.findAll();
        
        events = applyFilters(user, events, request.filters());
        
        events = applyOrdering(events, request.order());
        
        return new EventsResponse(events);
    }
    
    
    private List<Event> applyFilters(
            User user,
            List<Event> events,
            SearchFilters filters
    ) {
        
        if (filters == null) {
            return events;
        }
        
        // Niveles de usuario
        Set<String> userLevels = user.getDeportiveProfile()
                .getFavDisciplines()
                .stream()
                .map(fd -> fd.getCurrentLevel().getDescription())
                .collect(Collectors.toSet());
        
        // Disciplinas
        Set<String> disciplineIds = resolveDisciplineIds(user, filters);
        
        return events.stream()
                
                // PRECIO
                .filter(e -> filters.price() == null ||
                        e.getPriceDetails().getPrice().compareTo(filters.price()) == 0)
                
                // EVENTOS ACTIVOS
                .filter(e -> e.getStartsAt().isAfter(Instant.now()))
                
                // FECHA (DÍA)
                .filter(e -> filters.day() == null ||
                        e.getStartsAt()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .isEqual(filters.day()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()))
                
                // CAMPO SEARCH
                .filter(e -> filters.search() == null ||
                        normalize(e.getSearch()).contains(normalize(filters.search())))
                
                // DISCIPLINAS  *** METER COMPROBACION PREVIA CASO SIN FILTROS ***
                .filter(e -> disciplineIds.isEmpty() ||
                        disciplineIds.contains(e.getDisciplineId()))
                
                // NIVELES
                .filter(e -> filters.levels() == null || filters.levels().isEmpty()
                        ? userLevels.contains(e.getLevel().getDescription())
                        : filters.levels().contains(e.getLevel().getDescription()))
                
                .toList();
    }
    
    
    private Set<String> resolveDisciplineIds(User user, SearchFilters filters) {
        
        // Filtro explícito del front
        if (filters != null
                && filters.disciplinesNames() != null
                && !filters.disciplinesNames().isEmpty()) {
            
            return filters.disciplinesNames().stream()
                    .map(name -> disciplineRepository.findByName(name)
                            .map(Discipline::getId)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
        
        // Fallback usuario
        if (user.getDeportiveProfile() != null
                && user.getDeportiveProfile().getFavDisciplines() != null) {
            
            return user.getDeportiveProfile()
                    .getFavDisciplines()
                    .stream()
                    .map(FavDisciplines::getDisciplineId)
                    .collect(Collectors.toSet());
        }
        
        // Sin filtros
        return Set.of();
    }
    
    
    private static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
    
    
    private List<Event> applyOrdering(
            List<Event> events,
            SearchOrderCriteria order
    ) {
        // TIEMPO
        if(order.isTime()) {
           return orderByClosestDate(events);
        }
        
        // PRECIO
        if (order.price() != null) {
            return orderByPrice(events);
        }
        
        // NIVEL
        if (order.level() != null) {
            return orderByLevel(events);
        }
        
        // PROXIMIDAD POR DEFECTO
        return orderByProximity(events, order.coords());
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
    
    
    private double distance(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        double R = 6371; // km
        
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