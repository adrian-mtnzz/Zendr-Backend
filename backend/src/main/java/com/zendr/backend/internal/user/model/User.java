package com.zendr.backend.internal.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String profileImg;
    private String email;
    private String password;
    private DeportiveProfile deportiveProfile;
    private Penalties penalties;
    private LocalDate createdAt;
    private LocalDate bod;
    private String rol;
    private String QRCode;
}
