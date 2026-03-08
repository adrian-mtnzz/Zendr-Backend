package com.zendr.backend.internal.user.model;

import com.zendr.backend.internal.user.model.enums.SubcriptionStatus;
import com.zendr.backend.internal.user.model.enums.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    @Indexed(unique = true, sparse = true)
    @NotNull(message = "stripeSubscriptionId no puede ser nula")
    private String stripeSubscriptionId;

    @NotNull(message = "El status no puede ser nulo")
    private SubcriptionStatus status;

    @NotNull(message = "El tipo no puede ser nulo")
    private SubscriptionType type;

    @Builder.Default
    @NotNull(message = "La autorenovación no puede ser nula")
    private boolean selfRenewal = false;

    @NotNull(message = "La fecha de expiración no puede ser nula")
    private Date expirationDate;
}
