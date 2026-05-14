package com.zendr.backend.internal.event.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record SearchEventsRequest(
        
        @NotBlank(message = "El ID de usuario no puede estar vacío")
        String userId,
        double[] coords,
        @Valid
        SearchFilters filters,
        
        @Valid
        SearchOrderCriteria order

) {
    
    public SearchEventsRequest {
        
        if (coords != null && coords.length != 2) {
            throw new IllegalArgumentException(
                    "Las coordenadas deben tener exactamente 2 elementos y no puedes ser nulas"
            );
        }
        coords = coords != null ? coords.clone() : null;
        
        if (filters == null) {
            filters = SearchFilters.defaultOrder();
        }
        if (order == null) {
            order = SearchOrderCriteria.defaultOrder();
        }
    }
}