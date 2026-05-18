package com.zendr.backend.internal.booking.model;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {
    
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    
    @NotBlank(message = "El ID de usuario no puede estar vacío")
    private String userId;
    
    @NotBlank(message = "El ID del evento asociado no puede estar vacío")
    private String eventId;
    
    @Builder.Default
    private BookingStatus status = BookingStatus.REGISTERED;
    
    @Valid
    private BookingPaymentDetails payment;

    private Instant deletedAt;
    
    @Getter
    @RequiredArgsConstructor
    public enum BookingStatus {
        REGISTERED("Registrado"),
        CANCELED("Cancelado"),
        ASSISTED("Asistido"),
        UNNASSISTED("Sin asistencia"),
        CANCELED_BY_USER("Cancelado por usuario");// No mostrar en listas de eventos
        
        @JsonValue
        private final String description;
    }
}
