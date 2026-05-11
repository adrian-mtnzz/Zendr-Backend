package com.zendr.backend.internal.token.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    String userId,
    String deviceId
) {
}
