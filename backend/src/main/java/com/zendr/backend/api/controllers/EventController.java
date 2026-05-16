package com.zendr.backend.api.controllers;

import com.zendr.backend.internal.event.dtos.CreateEventRequest;
import com.zendr.backend.internal.event.dtos.EventResponse;
import com.zendr.backend.internal.event.dtos.SearchEventsRequest;
import com.zendr.backend.internal.event.dtos.SearchEventDTO;
import com.zendr.backend.services.event.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    
    private final EventService service;
    
    
    @PostMapping()
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(
                service.save(request, file)
        );
    }
    
    @PostMapping("/search")
    public ResponseEntity<Page<SearchEventDTO>> searchOrders(
            @Valid @RequestBody SearchEventsRequest request,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.filterAndOrderAllEvents(request, pageable));
    }
}