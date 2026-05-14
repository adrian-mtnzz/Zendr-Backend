package com.zendr.backend.services.geocoding;

import com.zendr.backend.internal.event.model.EventLocation;

public interface GeocodingService {
    EventLocation.Coordinates getCoordinates(String address, String city, String region, String countryCode);
}
