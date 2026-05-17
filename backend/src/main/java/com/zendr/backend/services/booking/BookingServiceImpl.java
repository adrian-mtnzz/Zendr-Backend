package com.zendr.backend.services.booking;

import com.zendr.backend.internal.booking.model.Booking;
import com.zendr.backend.internal.booking.model.dtos.BookingResponse;
import com.zendr.backend.internal.booking.repository.BookingRepository;
import com.zendr.backend.internal.event.model.Event;
import com.zendr.backend.internal.event.repository.EventRepository;
import com.zendr.backend.internal.user.model.User;
import com.zendr.backend.internal.user.repository.UserRepository;
import com.zendr.backend.services.event.EventService;
import com.zendr.backend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookingRepository repository;
    
    
    
    @PreAuthorize("""
    @userRepository.findById(#userId).get().email == authentication.name
    """)
    @Transactional
    public BookingResponse save(String eventId, String userId) {
        
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new IllegalArgumentException("Evento no encontrado"));
        
        if (!userRepository.existsById(userId))
            throw new IllegalArgumentException("Usuario no encontrado");
        
        if (userService.getPenalties(userId).isPresent() && userService.getPenalties(userId).get().getBan().isBanned())
            throw new IllegalArgumentException("El usuario esta baneado");
        
        if (event.getStatus() != Event.EventStatus.ACTIVE)
            throw new IllegalArgumentException("El evento disponible para registrarse");
        
        if (event.getCapacity().isFull())
            throw new IllegalArgumentException("No se puede registrar en el evento porque esta lleno");
        
        if (repository.findByUserIdAndStatusNot(userId, Booking.BookingStatus.CANCELED_BY_USER) != null) {
            throw new IllegalArgumentException("Ya existe un reserva para este evento");
        }
        
        Booking booking = Booking.builder()
                                .eventId(eventId)
                                .userId(userId)
                                .status(Booking.BookingStatus.REGISTERED)
                                .build();
        
        Booking savedBooking = repository.save(booking);
        
        int actualBookings = event.getCapacity().getActualBookings() + 1;
        event.getCapacity().setActualBookings(actualBookings);
        
        eventRepository.save(event);
        
        return BookingResponse.builder()
                .id(savedBooking.getId())
                .eventId(savedBooking.getEventId())
                .userId(savedBooking.getUserId())
                .status(savedBooking.getStatus())
                .build();
    }
    
    
    @PreAuthorize("""
    @userRepository.findById(#userId).get().email == authentication.name
    """)
    public Boolean cancel(String eventId, String id) {
        
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new IllegalArgumentException("Evento no encontrado"));
        
        Booking booking = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Reserva no encontrada"));
        
        User user = userRepository.findById(booking.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Usuario no encontrado"));
                
        
        booking.setStatus(Booking.BookingStatus.CANCELED_BY_USER);
        
        if (event.getStartsAt().isBefore(Instant.now().plusSeconds(3600))) {
            userService.applyPenalty(user.getId());
        }
        
        return true;
    }
}
