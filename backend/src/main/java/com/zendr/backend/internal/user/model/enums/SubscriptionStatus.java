package com.zendr.backend.internal.user.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {
    ACTIVE("Activa"),
    SUSPENDED("Suspendida"),
    BANNED("Baneada");

    @JsonValue
    private final String description;
}
