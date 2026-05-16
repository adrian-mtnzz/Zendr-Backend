package com.zendr.backend.services.geocoding;

import com.zendr.backend.internal.event.dtos.tomtom.*;
import com.zendr.backend.internal.event.model.EventLocation;
import com.zendr.backend.internal.weather.model.dtos.GeocodingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeocodingServiceImpl implements GeocodingService {
    
    @Value("${application.tomtom.api-key}")
    private String geocodeKey;
    
    @Value("${application.tomtom.search-key}")
    private String searchKey;
    
    private final WebClient tomTomWebClient;
    
    public EventLocation.Coordinates getCoordinates(
            String address,
            String city,
            String region,
            String countryCode
    ) {
        
        String query = String.format(
                "%s, %s, %s, %s",
                address,
                city,
                region,
                countryCode
        );
        
        GeocodingResponse response = tomTomWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/2/geocode/{query}.json")
                        .queryParam("storeResult", false)
                        .queryParam("typeahead", true)
                        .queryParam("limit", 1)
                        .queryParam("countrySet", countryCode)
                        .queryParam("view", "Unified")
                        .queryParam("key", geocodeKey)
                        .build(query)
                )
                .retrieve()
                .bodyToMono(GeocodingResponse.class)
                .block();
        
        if (response == null || response.results().isEmpty()) {
            throw new IllegalArgumentException("No se han encontrado las coordenadas para esa ubicación");
        }
        
        GeocodingResponse.Position position =
                response.results().getFirst().position();
        
        return new EventLocation.Coordinates(
                position.lon(),
                position.lat()
        );
    }
    
    public Page<LocationResultDTO>  getLocationsBySearch(String search, Pageable pageable) {
        
        TomTomSearchResponse response = tomTomWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/2/search/{query}.json")
                        .queryParam("typeahead", true)
                        .queryParam("limit", pageable.getPageSize())
                        .queryParam("ofs", pageable.getOffset())
                        .queryParam("minFuzzyLevel", 1)
                        .queryParam("maxFuzzyLevel", 2)
                        .queryParam("view", "Unified")
                        .queryParam("relatedPois", "off")
                        .queryParam("idxSet", "Geo,Addr,Str,PAD,POI")
                        .queryParam("countrySet", "ES")
                        .queryParam("key", searchKey)
                        .build(search)
                )
                .retrieve()
                .bodyToMono(TomTomSearchResponse.class)
                .block();
        
        if (response == null || response.results() == null) {
            return new PageImpl<>(List.of(), pageable, 0);
        }
        
        List<LocationResultDTO> results = response.results()
                .stream()
                .map(this::toDTO)
                .toList();
        
        int total = response.summary() != null
                ? response.summary().totalResults()
                : results.size();
        
        return new PageImpl<>(
                results,
                pageable,
                total
        );
        
    }
    
    private LocationResultDTO toDTO(TomTomResult r) {
        
        return new LocationResultDTO(
                r.id(),
                r.type(),
                r.score(),
                Optional.ofNullable(r.address())
                        .map(a -> new AddressDTO(
                                a.streetNumber(),
                                a.streetName(),
                                a.municipality(),
                                a.postalCode(),
                                a.country(),
                                a.countryCode(),
                                a.freeformAddress()
                        ))
                        .orElse(null),
                Optional.ofNullable(r.position())
                        .map(p -> new PositionDTO(p.lat(), p.lon()))
                        .orElse(null)
        );
    }
}
