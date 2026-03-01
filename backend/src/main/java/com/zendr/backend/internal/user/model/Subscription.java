package com.zendr.backend.internal.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    private String stripeSubcriptionId;
    private String status;
    private String type;
    private boolean selfRenewal;
    private Date expirationDate;
}
