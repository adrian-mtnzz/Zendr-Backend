package com.zendr.backend.internal.booking.model;


import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingPaymentDetails {
    
    @Indexed(unique = true, sparse = true)
    @NotBlank(message = "El id de la transacción no puede ser nulo")
    private String transactionId;
    
    @NotNull(message = "El estado de la transacción no puede ser nulo")
    private BookingTransactionStatus status;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser menor que 0")
    private BigDecimal amount;
    
    @NotNull(message = "La fecha en la que se realizó el pago no puede ser nula")
    private Instant paidAt;
    
    @Getter
    @RequiredArgsConstructor
    public enum BookingTransactionStatus {
        SUCCESS("Exitosa"),
        SUSPENDED("Suspendida"),
        DENIED("Denegada");
        
        @JsonValue
        private final String description;
    }
}
