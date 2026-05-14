package com.zendr.backend.services.weather;

import com.zendr.backend.internal.weather.model.Weather;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Override
    public Weather getWeatherForCoordinates(double longitud, double latitud) {
        return null;
    }
}
