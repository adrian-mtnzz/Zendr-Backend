package com.zendr.backend.internal.event.dtos;

import jakarta.validation.Valid;

import java.math.BigDecimal;

public record SearchOrderCriteria(
        
        Boolean proximity,
        Boolean time,
        Boolean price,
        Boolean level

) {
    
    public static SearchOrderCriteria defaultOrder() {
        return new SearchOrderCriteria(true, false, false, false);
    }
    
    public boolean isProximity() {
        return Boolean.TRUE.equals(proximity);
    }
    public boolean isTime() {
        return Boolean.TRUE.equals(time);
    }
    public boolean isPrice() { return Boolean.TRUE.equals(time); }
    public boolean isLevel() {return Boolean.TRUE.equals(level); }
}