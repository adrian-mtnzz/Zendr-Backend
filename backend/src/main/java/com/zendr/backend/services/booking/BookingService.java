package com.zendr.backend.services.booking;

import com.zendr.backend.internal.booking.model.dtos.BookingResponse;

public interface BookingService {
    BookingResponse save(String eventId, String UserId);
    Boolean cancel(String eventId, String id);
}
