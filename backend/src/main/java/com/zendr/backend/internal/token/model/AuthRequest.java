package com.zendr.backend.internal.token.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El email no es válido")
        String email,
        
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,
        
        String deviceId,
        String platform,
        String deviceModel,
        String ipAddress
) {
}
