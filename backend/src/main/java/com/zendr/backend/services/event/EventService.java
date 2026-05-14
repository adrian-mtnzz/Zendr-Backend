package com.zendr.backend.services.event;

import com.zendr.backend.internal.event.dtos.EventsSearchRequest;
import com.zendr.backend.internal.event.dtos.SearchEventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    Page<SearchEventDTO> filterAndOrderAllEvents(EventsSearchRequest request, Pageable pageable);
}
