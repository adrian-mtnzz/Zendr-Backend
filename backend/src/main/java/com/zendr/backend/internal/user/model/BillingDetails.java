package com.zendr.backend.internal.user.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BillingDetails {

    @Indexed(unique = true, sparse = true)
    @NotNull(message = "stripeCustomerId no puede ser nulo")
    private String stripeCustomerId;

    @Valid
    @NotNull (message = "La subscripcion no puede ser nula")
    private Subscription subscription;
}
