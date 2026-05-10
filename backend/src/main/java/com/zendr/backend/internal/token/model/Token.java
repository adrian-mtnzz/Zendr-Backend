package com.zendr.backend.internal.token.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tokens")
public class Token {
    
    @Id
    @Setter(AccessLevel.NONE)
    private Long id;
    
    @NotBlank(message = "El ID de usuario no puede estar vacío")
    private String userId;
    
    @Indexed(unique = true)
    @NotBlank(message = "El token no puede estar vacío")
    private String token;
    
    @Builder.Default
    private TokenType tokenType = TokenType.BEARER;
    
    @NotNull(message = "La fecha de creación es obligatoria")
    private Instant createdAt;
    
    @NotNull(message = "La fecha de expiración es obligatoria")
    private Instant expiresAt;
    
    @NotNull(message = "El estado de expiración es obligatorio")
    private boolean isExpired;
    
    @NotNull(message = "El estado revocado es obligatorio")
    private boolean isRevoked;
    
    @NotNull(message = "El id del dispositivo es obligatorio")
    private String deviceId;
    
    public enum TokenType {
        BEARER
    }
}
