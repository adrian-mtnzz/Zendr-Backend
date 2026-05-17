package com.zendr.backend.internal.event.dtos;

import com.zendr.backend.internal.event.model.EventCapacity;
import com.zendr.backend.internal.event.model.EventLocation;
import com.zendr.backend.internal.event.model.EventPriceDetails;
import com.zendr.backend.internal.weather.model.Weather;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;

@Builder
public record EventDetailsResponse(
        String id,
        String eventImgUrl,
        String name,
        String placeCommonName,
        String address,
        String city,
        String region,
        String countryCode,
        String zip,
        String description,
        String monitorId,
        String monitorProfileImg,
        String monitorName,
        String disciplineId,
        String level,
        String waitListId,
        Instant startsAt,
        Duration duration,
        Weather weather,
        EventLocation location,
        EventPriceDetails priceDetails,
        EventCapacity capacity,
        String status,
        boolean isReserved,
        String bookingStatus,
        String bookingId
) {
}
