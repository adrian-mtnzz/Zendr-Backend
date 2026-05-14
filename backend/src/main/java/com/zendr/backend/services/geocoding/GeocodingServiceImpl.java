package com.zendr.backend.services.geocoding;

import com.zendr.backend.internal.event.model.EventLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeocodingServiceImpl implements GeocodingService {
    
    
    public EventLocation.Coordinates getCoordinates() {
        return new EventLocation.Coordinates(0,0,0);
    }
}
