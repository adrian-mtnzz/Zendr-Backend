package com.zendr.backend.internal.event.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

public record SearchEventDTO(
        
        double temparatureInCelsius,
        double distance,
        String name,
        String placeCommonName,
        String disciplineName,
        String disciplineLevel,
        Instant date,
        BigDecimal price,
        String currencySymbol
        
){
}
