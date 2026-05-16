package com.zendr.backend.internal.event.dtos.tomtom;

public record Summary(
        String query,
        int numResults,
        int totalResults
) {}