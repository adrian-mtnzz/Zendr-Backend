package com.zendr.backend.internal.event.dtos;

import com.zendr.backend.internal.event.model.Event;

import java.util.List;

public record EventsResponse(
    List<SearchEventDTO> events
    
) {
}