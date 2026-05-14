package com.zendr.backend.services.weather;

import com.zendr.backend.internal.weather.model.Weather;

public interface WeatherService {
    Weather getCurrentWeather(double longitud, double latitud);
}
