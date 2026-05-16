package com.zendr.backend.internal.token.model;

import com.zendr.backend.internal.user.model.BillingDetails;
import com.zendr.backend.internal.user.model.DeportiveProfile;
import com.zendr.backend.internal.user.model.Penalties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "El nombre de usuario no puede estar vacío")
        String username,
        
        @NotBlank(message = "El nombre no puede estar vacío")
        String name,
        
        @NotBlank(message = "Los apellidos no pueden estar vacíos")
        String surname,
        
        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El email no es válido")
        String email,
        
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,
        
        @Valid
        DeportiveProfile deportiveProfile,
        
        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        LocalDate dob,
        
        @NotNull(message = "La plataforma del dispositivo es obligatoria")
        String platform,
        
        @NotNull(message = "El modelo del dispositivo es obligatorio")
        String deviceModel,
        
        @NotNull(message = "La dirección ip del dispositivo es obligatoria")
        String ipAddress,
        
        @NotNull(message = "El código de verificación no puede ser nulo")
        String code
) {
}
