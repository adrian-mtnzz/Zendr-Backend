package com.zendr.backend.internal.weather.model.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeocodingResponse(
        List<Result> results
) {
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            Position position
    ) {
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Position(
            double lat,
            double lon
    
    ) {
    }
}
