package com.zendr.backend.internal.event.dtos.tomtom;

public record TomTomResult(
        String id,
        String type,
        double score,
        Address address,
        Position position
) {}