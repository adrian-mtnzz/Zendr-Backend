package com.zendr.backend.internal.weather.model.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherResponse(
        
        List<WeatherInfo> weather,
        Main main,
        long dt

) {
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WeatherInfo(
            
            int id,
            String main,
            String description,
            String icon
    
    ) {
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Main(
            
            double temp,
            double feels_like,
            int pressure,
            int humidity
    
    ) {
    }
}