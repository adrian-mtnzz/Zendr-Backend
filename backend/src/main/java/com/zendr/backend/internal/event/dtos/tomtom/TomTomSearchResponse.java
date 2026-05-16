package com.zendr.backend.internal.event.dtos.tomtom;

import java.util.List;

public record TomTomSearchResponse(
        List<TomTomResult> results,
        Summary summary
) {}