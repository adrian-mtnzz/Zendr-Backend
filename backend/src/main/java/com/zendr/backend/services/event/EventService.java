package com.zendr.backend.services.event;

import com.zendr.backend.internal.event.dtos.EventsResponse;
import com.zendr.backend.internal.event.dtos.EventsSearchRequest;

public interface EventService {
    EventsResponse filterAndOrderAllEvents(EventsSearchRequest request);
}
