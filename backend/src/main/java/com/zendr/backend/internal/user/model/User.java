package com.zendr.backend.internal.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zendr.backend.internal.user.model.enums.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "users")
public class User {

    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    @NotNull(message = "El nombre de usuario no puede estar vacío")
    @Indexed(unique = true)
    private String username;

    @NotNull(message = "El nombre no puede estar vacío")
    private String name;

    @NotNull(message = "Los apellidos no pueden estar vacíos")
    private String surname;

    private String profileImg;

    @NotNull(message = "El email no puede estar vacío")
    @Indexed(unique = true)
    private String email;

    @NotNull(message = "La contraseña no puede estar vacía")
    private String password;

    @Valid
    private DeportiveProfile deportiveProfile;

    @Valid
    @NotNull(message = "Las penalizaciones no pueden estar vacías")
    private Penalties penalties;

    @Valid
    private BillingDetails billingDetails;

    @CreatedDate
    private LocalDate createdAt;

    @NotNull
    private LocalDate bod;

    @NotNull
    @Builder.Default
    @NotNull(message = "El rol no puede estar vacío")
    private UserRole role = UserRole.USER;

    @Indexed(unique = true, sparse = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String QRCode;
}
