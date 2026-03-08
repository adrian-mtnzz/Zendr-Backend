package com.zendr.backend.internal.user;

import com.zendr.backend.internal.user.model.*;
import com.zendr.backend.internal.user.model.enums.FavDisciplinesCurrentLevel;
import com.zendr.backend.internal.user.model.enums.SubcriptionStatus;
import com.zendr.backend.internal.user.model.enums.SubscriptionType;
import com.zendr.backend.internal.user.model.enums.UserRole;
import com.zendr.backend.internal.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("dev")
public class UserRepositoryTestNoContainers {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        // CREACION DE UN USUARIO
        // Perfil deportivo
        List<FavDisciplines> favDisciplines = List.of(FavDisciplines.builder()
                        .disciplineId("OID DE PRUEBA")
                        .currentLevel(FavDisciplinesCurrentLevel.AVANZADO)
                        .build(),
                FavDisciplines.builder()
                        .disciplineId("OID DE PRUEBA 2")
                        .currentLevel(FavDisciplinesCurrentLevel.EXPERTO)
                        .build()
        );

        DeportiveProfile dp = DeportiveProfile.builder()
                .favDisciplines(favDisciplines)
                .previousInjuries("Tiene que llevar cuidado con el estres")
                .build();

        // Penalizaciones
        BanStatus banStatus = BanStatus.builder().build();

        Penalties penalties = Penalties.builder()
                .ban(banStatus)
                .build();

        // Detalles de pagos
        Subscription subscription = Subscription.builder()
                .stripeSubscriptionId("3232kgr45Ad9")
                .status(SubcriptionStatus.ACTIVE)
                .type(SubscriptionType.MONITOR)
                .selfRenewal(true)
                .expirationDate(Date.from(Instant.now().plus(30, DAYS)))
                .build();

        BillingDetails billingDetails = BillingDetails.builder()
                .stripeCustomerId("123f45FD7j231")
                .subscription(subscription)
                .build();


        User user = User.builder()
                .name("Sergio Ruiz")
                .username("sergio_reactivo")
                .profileImg("https://stanforddaily.com/wp-content/uploads/2025/01/IMG_1172.jpg")
                .email("sergioreactpro@gmail.com")
                .password("Admin1234!!")
                .deportiveProfile(dp)
                .penalties(penalties)
                .billingDetails(billingDetails)
                .bod(LocalDate.of(1984, 1, 17))
                .role(UserRole.ADMIN)
                .build();

        userRepository.save(user);
    }

    @Test
    void shouldFindUserById() {

        User user = userRepository.findByEmail("sergioreactpro@gmail.com")
                .orElseThrow(() -> new AssertionError("Usuario no encontrado por email"));

        User result = userRepository.findById(user.getId())
                .orElseThrow(() -> new AssertionError("Usuario no encontrado por ID"));

        assertEquals("Sergio Ruiz", result.getName());
    }
}
