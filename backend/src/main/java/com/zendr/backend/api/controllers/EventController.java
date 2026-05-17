package com.zendr.backend.api.controllers;

import com.zendr.backend.internal.booking.model.dtos.BookingResponse;
import com.zendr.backend.internal.event.dtos.*;
import com.zendr.backend.services.booking.BookingService;
import com.zendr.backend.services.event.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    
    private final EventService service;
    private final BookingService bookingService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestPart("request") CreateEventRequest request,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(
                service.save(request, file)
        );
    }
    
    
    @PostMapping("/{id}/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @PathVariable String id,
            @RequestParam String userId) {
        
        return ResponseEntity.ok(bookingService.save(id, userId));
    }
    
    
    @PatchMapping("/{id}/bookings/{bookingId}")
    public ResponseEntity<Map<String, Boolean>> cancelBooking(
            @PathVariable String id,
            @PathVariable String bookingId
    ) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("cancelled", bookingService.cancel(id, bookingId));
        
        return ResponseEntity.ok(response);
    }
    
    
    @PostMapping("/search")
    public ResponseEntity<Page<SearchEventDTO>> searchEvents(
            @Valid @RequestBody SearchEventsRequest request,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.filterAndOrderAllEvents(request, pageable));
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<EventDetailsResponse> getEventDetails(@PathVariable String id) {
        return ResponseEntity.ok(
                service.getEventDetails(id)
        );
    }
    
    
    @PatchMapping("/{id}/")
    public ResponseEntity<EventDetailsResponse> updateEvent(
            @PathVariable String id,
            @RequestPart MultipartFile file,
            @RequestPart UpdateEventRequest request
    ) {
        return ResponseEntity.ok(
                service.update(id, request, file)
        );
    }
    
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Boolean>> cancelEvent(
            @PathVariable String id) {
            
        Map<String, Boolean> response = new HashMap<>();
        response.put("cancelled", service.cancelEvent(id));
        return ResponseEntity.ok(response);
    }
}