package com.zendr.backend.internal.user.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionType {
    MONITOR("Monitor"),
    MANAGER("Manager");

    @JsonValue
    private final String description;
}
