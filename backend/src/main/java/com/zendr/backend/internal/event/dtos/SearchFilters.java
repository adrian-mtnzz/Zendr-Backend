package com.zendr.backend.internal.event.dtos;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SearchFilters(
        
        BigDecimal price,
        List<String> disciplinesNames,
        List<String> levels,
        Instant day,
        String search

) {
    public static SearchFilters defaultOrder() {
        return new SearchFilters(null,null,null,null,null);
    }
}