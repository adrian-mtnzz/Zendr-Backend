package com.zendr.backend.internal.event.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

public record SearchEventDTO(
        
        String id,
        double temparatureInCelsius,
        String weatherIconUrl,
        double distance,
        String eventImageUrl,
        String name,
        String placeCommonName,
        String disciplineName,
        String disciplineLevel,
        Instant date,
        BigDecimal price,
        String currencySymbol,
        boolean isReserved,
        String bookingStatus,
        String bookingId
        
){
}
