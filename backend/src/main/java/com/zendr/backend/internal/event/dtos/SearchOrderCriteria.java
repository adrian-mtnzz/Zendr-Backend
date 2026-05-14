package com.zendr.backend.internal.event.dtos;

import jakarta.validation.Valid;

import java.math.BigDecimal;

public record SearchOrderCriteria(
        
        Boolean proximity,
        Boolean time,
        BigDecimal price,
        String level,
        double[] coords

) {
    
    public SearchOrderCriteria {
        
        if (coords != null && coords.length != 3) {
            throw new IllegalArgumentException(
                    "Las coordenadas deben tener exactamente 3 elementos"
            );
        }
        coords = coords != null ? coords.clone() : null;
    }
    
    public static SearchOrderCriteria defaultOrder() {
        return new SearchOrderCriteria(true, false, null, null, null);
    }
    
    public boolean isProximity() {
        return Boolean.TRUE.equals(proximity);
    }
    
    public boolean isTime() {
        return Boolean.TRUE.equals(time);
    }
}