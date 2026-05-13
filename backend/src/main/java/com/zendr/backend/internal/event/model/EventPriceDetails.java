package com.zendr.backend.internal.event.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@NoArgsConstructor
public class EventPriceDetails {
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser menor que 0")
    private BigDecimal price;
    
    @NotNull(message = "La moneda no puede ser nula")
    private Currency currency;
    
    
    @Builder
    public EventPriceDetails(BigDecimal price, String currency) {
        this.price = price;
        this.currency = Currency.getInstance(currency);
    }
}
