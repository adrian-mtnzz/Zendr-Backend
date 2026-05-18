package com.zendr.backend.internal.booking.repository;

import com.zendr.backend.internal.booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByEventId(String eventId);
    List<Booking> findByUserId(String userId);
    Optional<Booking> findByUserIdAndStatus(String userId, Booking.BookingStatus status);
    Optional<Booking> findByUserIdAndStatusNot(String userId, Booking.BookingStatus status);
    
    Optional<Booking> findByUserIdAndEventIdAndStatusNot(
            String userId, String eventId, Booking.BookingStatus status
    );

    @Query("{ 'userId': ?0, 'eventId': ?1, 'status': { '$ne': ?2 }, 'deletedAt': null }")
    Optional<Booking> findByUserIdAndEventIdAndStatusNotAndDeletedAtNull(
            String userId, String eventId, Booking.BookingStatus status
    );
    Boolean existsByUserIdAndEventIdAndStatus(String userId, String eventId, Booking.BookingStatus status);
    Boolean existsByUserIdAndStatus(String userId, Booking.BookingStatus status);
}
