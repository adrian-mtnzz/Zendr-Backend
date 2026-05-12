package com.zendr.backend.internal.weather.repository;

import com.zendr.backend.internal.weather.model.Weather;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends MongoRepository<Weather, String> {
}
