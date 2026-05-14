package com.zendr.backend.internal.event.model;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
public class EventCapacity {
    
    @Min(value=1, message = "La capacidad del evento no puede ser nula")
    private int maxCapacity;
    
    @Min(value = 0, message = "Las reservas actuales no pueden ser negativas")
    private int actualBookings;
    
    @NotNull(message = "El estado de ocupación del evento no puede ser nulo")
    private boolean isFull;
    
    
    @Builder
    public EventCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.actualBookings = 0;
        this.isFull = false;
    }
}
