package com.zendr.backend.internal.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BanStatus {

    @NotNull
    @Builder.Default
    private boolean isBanned = false;

    private Instant expiresAt;
}
