package com.zendr.backend.internal.event.dtos;

import com.zendr.backend.internal.event.model.EventCapacity;
import com.zendr.backend.internal.event.model.EventLocation;
import com.zendr.backend.internal.event.model.EventPriceDetails;
import com.zendr.backend.internal.weather.model.Weather;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public record EventResponse(
        
        String id,
        String eventImgUrl,
        String name,
        String placeCommonName,
        String address,
        String city,
        String region,
        String country,
        String zip,
        String description,
        String monitorId,
        String disciplineId,
        String level,
        String waitListId,
        Instant startsAt,
        Duration duration,
        Instant endsAt,
        Weather weather,
        EventLocation location,
        EventPriceDetails priceDetails,
        EventCapacity capacity,
        String status
        
) {
}