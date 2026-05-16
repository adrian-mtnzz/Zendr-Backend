package com.zendr.backend.services.event;

import com.zendr.backend.internal.event.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    EventResponse save(CreateEventRequest request, MultipartFile file);
    Page<SearchEventDTO> filterAndOrderAllEvents(SearchEventsRequest request, Pageable pageable);
    EventDetailsResponse getEventDetails(String eventId);
    public boolean cancelEvent(String eventId);
}
