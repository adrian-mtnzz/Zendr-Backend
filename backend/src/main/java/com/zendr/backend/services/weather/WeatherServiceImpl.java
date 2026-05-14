package com.zendr.backend.services.weather;

import com.zendr.backend.internal.weather.model.Weather;
import com.zendr.backend.internal.weather.model.dtos.GeocodingResponse;
import com.zendr.backend.internal.weather.model.dtos.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    
    @Value("${application.openweather.api-key}")
    private String apiKey;
    private final WebClient openWeatherWebClient;
    
    public Weather getCurrentWeather(double longitude, double latitude) {
        
        WeatherResponse response = openWeatherWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/weather")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .queryParam("lang", "es")
                        .build()
                )
                .retrieve()
                .bodyToMono(WeatherResponse.class)
                .block();
        
        if (response == null || response.weather() == null || response.weather().isEmpty()) {
            throw new IllegalArgumentException("No se ha encontrado el clima");
        }
        
        WeatherResponse.WeatherInfo info = response.weather().getFirst();
        
        return Weather.builder()
                .temperatureInCelsius(response.main().temp())
                .condition(info.main())
                .description(info.description())
                .iconUrl(buildIconUrl(info.icon()))
                .isAptOutdoors(isWeatherApt(info.main()))
                .lastUpdate(Instant.ofEpochSecond(response.dt()))
                .build();
    }
    
    private String buildIconUrl(String icon) {
        return "https://openweathermap.org/img/wn/" + icon + "@2x.png";
    }
    
    private boolean isWeatherApt(String main) {
        
        return switch (main.toLowerCase()) {
            
            case "thunderstorm",
                 "tornado",
                 "snow" -> false;
            
            default -> true;
        };
    }
}
