package com.zendr.backend.internal.event.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record SearchEventsRequest(
        
        @NotBlank(message = "El ID de usuario no puede estar vacío")
        String userId,
        
        @Valid
        SearchFilters filters,
        
        @Valid
        SearchOrderCriteria order

) {
    
    public SearchEventsRequest {
        
        if (filters == null) {
            filters = SearchFilters.defaultOrder();
        }
        if (order == null) {
            order = SearchOrderCriteria.defaultOrder();
        }
    }
}