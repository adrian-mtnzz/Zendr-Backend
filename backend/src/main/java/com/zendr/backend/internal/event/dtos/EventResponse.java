package com.zendr.backend.internal.event.dtos;

import com.zendr.backend.internal.event.model.EventCapacity;
import com.zendr.backend.internal.event.model.EventLocation;
import com.zendr.backend.internal.event.model.EventPriceDetails;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public record EventResponse(
        
        String id,
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
        String weatherId,
        String waitListId,
        Instant startsAt,
        Duration duration,
        Instant endsAt,
        EventLocation location,
        EventPriceDetails priceDetails,
        EventCapacity capacity,
        String status
        
) {
}