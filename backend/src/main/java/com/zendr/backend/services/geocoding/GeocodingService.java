package com.zendr.backend.services.geocoding;

import com.zendr.backend.internal.event.dtos.tomtom.LocationResultDTO;
import com.zendr.backend.internal.event.model.EventLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface GeocodingService {
    EventLocation.Coordinates getCoordinates(String address, String city, String region, String countryCode);
    Page<LocationResultDTO>  getLocationsBySearch(String search, Pageable pageable);
}
