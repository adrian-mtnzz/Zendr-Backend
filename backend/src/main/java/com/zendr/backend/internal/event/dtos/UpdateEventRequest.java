package com.zendr.backend.internal.event.dtos;

import java.math.BigDecimal;

public record UpdateEventRequest(
        String description,
        BigDecimal price
) {
}
