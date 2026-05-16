package com.zendr.backend.internal.booking.repository;

import com.zendr.backend.internal.booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByEventId(String eventId);
}
