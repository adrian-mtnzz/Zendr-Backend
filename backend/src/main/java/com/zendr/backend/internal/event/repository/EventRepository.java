package com.zendr.backend.internal.event.repository;

import com.zendr.backend.internal.event.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByMonitorIdAndStatus(String monitorId, Event.EventStatus status);
    List<Event> findByMonitorId(String monitorId);
}
