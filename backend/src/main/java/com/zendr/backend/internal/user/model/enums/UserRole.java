package com.zendr.backend.internal.user.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("Usuario"),
    MONITOR("Monitor"),
    MANAGER("Manager"),
    ADMIN("Administrador");

    @JsonValue
    private final String description;
}
