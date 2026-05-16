package com.zendr.backend.api.controllers;

import com.zendr.backend.internal.event.dtos.*;
import com.zendr.backend.services.event.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    
    private final EventService service;
    
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestPart("request") CreateEventRequest request,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(
                service.save(request, file)
        );
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
}