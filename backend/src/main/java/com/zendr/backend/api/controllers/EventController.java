package com.zendr.backend.api.controllers;

import com.zendr.backend.internal.event.dtos.EventsResponse;
import com.zendr.backend.internal.event.dtos.EventsSearchRequest;
import com.zendr.backend.services.event.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {
    
    private final EventService service;
    
    @PostMapping("/search")
    public ResponseEntity<EventsResponse> searchOrders(
            @Valid @RequestBody EventsSearchRequest request
    ) {
        return ResponseEntity.ok(service.filterAndOrderAllEvents(request));
    }
}