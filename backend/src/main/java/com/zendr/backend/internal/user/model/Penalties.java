package com.zendr.backend.internal.user.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Penalties {
    
    @Valid
    @Builder.Default
    private int warnings = 0;

    @Valid
    @NotNull(message = "Ban status es obligatorio")
    private BanStatus ban;
}
