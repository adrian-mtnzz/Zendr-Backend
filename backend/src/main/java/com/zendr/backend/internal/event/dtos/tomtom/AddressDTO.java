package com.zendr.backend.internal.event.dtos.tomtom;

public record AddressDTO(
        String streetNumber,
        String streetName,
        String municipality,
        String postalCode,
        String country,
        String countryCode,
        String freeformAddress
) {}