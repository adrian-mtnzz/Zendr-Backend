package com.zendr.backend.internal.event.dtos.tomtom;

public record LocationResultDTO(
        String id,
        String type,
        double score,
        AddressDTO address,
        PositionDTO position
) {}