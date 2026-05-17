package com.zendr.backend.internal.booking.model.dtos;

import com.zendr.backend.internal.booking.model.Booking;
import lombok.Builder;

@Builder
public record BookingResponse(
        String id,
        String userId,
        String eventId,
        Booking.BookingStatus status
) {
}
