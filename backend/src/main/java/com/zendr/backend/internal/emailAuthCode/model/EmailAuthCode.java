package com.zendr.backend.internal.emailAuthCode.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "emailAuthCodes")
public class EmailAuthCode {
    
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    
    @NotNull(message = "El email no puede estar vacío")
    @Indexed(unique = true)
    private String email;
    
    @NotNull(message = "El código no puede estar vacío")
    private String code;
    
    @Setter(AccessLevel.NONE)
    private int attemptsCounter;
    
    @Setter(AccessLevel.NONE)
    @Indexed(expireAfter = "0s")
    private Instant expiresAt;
    
    @Setter(AccessLevel.NONE)
    private Instant createdAt;
    
    @Setter(AccessLevel.NONE)
    private boolean isRevoked;
    
    @Builder
    public EmailAuthCode(String email, String code) {
        this.email = email;
        this.code = code;
        
        this.attemptsCounter = 0;
        this.createdAt = Instant.now();
        this.expiresAt = createdAt.plusSeconds(300);
        this.isRevoked = false;
    }
    
    public void increaseAttempts() {
        this.attemptsCounter++;
        if (this.attemptsCounter > 5) revoke();
    }
    
    public void revoke() {
        this.isRevoked = true;
    }
}
