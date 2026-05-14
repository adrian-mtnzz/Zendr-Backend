package com.zendr.backend.api.controllers;

import com.zendr.backend.internal.event.dtos.EventsResponse;
import com.zendr.backend.internal.event.dtos.EventsSearchRequest;
import com.zendr.backend.internal.event.dtos.SearchEventDTO;
import com.zendr.backend.services.event.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    
    private final EventService service;
    
    @PostMapping("/search")
    public ResponseEntity<Page<SearchEventDTO>> searchOrders(
            @Valid @RequestBody EventsSearchRequest request,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.filterAndOrderAllEvents(request, pageable));
    }
}