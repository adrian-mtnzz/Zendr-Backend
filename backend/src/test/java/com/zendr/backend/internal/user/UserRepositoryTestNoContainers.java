package com.zendr.backend.internal.user;

import com.zendr.backend.config.UserQRListener;
import com.zendr.backend.internal.user.model.*;
import com.zendr.backend.internal.user.model.enums.FavDisciplinesCurrentLevel;
import com.zendr.backend.internal.user.model.enums.SubscriptionStatus;
import com.zendr.backend.internal.user.model.enums.SubscriptionType;
import com.zendr.backend.internal.user.model.enums.UserRole;
import com.zendr.backend.internal.user.repository.UserRepository;
import com.zendr.backend.services.QRService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DataMongoTest
@ActiveProfiles("dev")
@Import(UserQRListener.class)
public class UserRepositoryTestNoContainers {

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private QRService qrService;

    private User savedUser;

    @BeforeEach
    void setUp() throws Exception {
        when(qrService.generateQRAsBase64(anyString())).thenReturn("QR_BASE64_TEST_DATA");
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
                .status(SubscriptionStatus.ACTIVE)
                .type(SubscriptionType.MONITOR)
                .selfRenewal(true)
                .expirationDate(Instant.now().plus(30, DAYS))
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

        this.savedUser = userRepository.save(user);
    }

    @Test
    void shouldFindUserAndHaveQRCode() throws Exception {
        User result = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new AssertionError("Usuario no encontrado"));

        assertEquals("Sergio Ruiz", result.getName());
        assertEquals("sergioreactpro@gmail.com", result.getEmail());

        assertNotNull(result.getQRCode(), "El QR debería haberse generado en el Listener");
        assertEquals("QR_BASE64_TEST_DATA", result.getQRCode());

        verify(qrService, times(1)).generateQRAsBase64(anyString());
    }
}
