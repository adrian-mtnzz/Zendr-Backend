package com.zendr.backend.services.event;

import com.zendr.backend.internal.event.dtos.CreateEventRequest;
import com.zendr.backend.internal.event.dtos.EventResponse;
import com.zendr.backend.internal.event.dtos.SearchEventsRequest;
import com.zendr.backend.internal.event.dtos.SearchEventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    EventResponse save(CreateEventRequest request);
    Page<SearchEventDTO> filterAndOrderAllEvents(SearchEventsRequest request, Pageable pageable);
}
