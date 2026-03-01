package com.zendr.backend.internal.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillingDetails {

    private String stripeCustomerId;
    private Subscription subscription;
}
