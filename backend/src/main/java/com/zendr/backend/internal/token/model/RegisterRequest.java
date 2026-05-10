package com.zendr.backend.internal.token.model;

public record RegisterRequest(
        String name,
        String email,
        String password,
        
        String platform,
        String deviceModel,
        String ipAddress
) {
}
