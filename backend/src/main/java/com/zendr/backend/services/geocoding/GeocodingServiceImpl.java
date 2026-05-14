package com.zendr.backend.services.geocoding;

import com.zendr.backend.internal.event.model.EventLocation;
import com.zendr.backend.internal.weather.model.dtos.GeocodingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GeocodingServiceImpl implements GeocodingService {
    
    @Value("${application.tomtom.api-key}")
    private String apiKey;
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
                        .queryParam("key", apiKey)
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
}
