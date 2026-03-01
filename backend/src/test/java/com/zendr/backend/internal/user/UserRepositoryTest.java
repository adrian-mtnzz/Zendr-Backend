package com.zendr.backend.internal.user;

import com.zendr.backend.internal.user.model.*;
import com.zendr.backend.internal.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("dev")
@Testcontainers
@Slf4j
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongo::getReplicaSetUrl);
    }


    @BeforeEach
    void setUp() {

        // Vaciar el repo de test
        userRepository.deleteAll();

        // CREACION DE UN USUARIO
        // Perfil deportivo
        Map<String, String> favDisciplines = Map.of(
                "Hacer pantallitas", "Avanzado",
                "Yoga", "Principiante"
        );

        DeportiveProfile dp = DeportiveProfile.builder()
                .favDisciplines(favDisciplines)
                .previousInjuries("Tiene que llevar cuidado con el estres")
                .build();

        // Penalizaciones
        Map<Boolean, Date> ban = Map.of(false, Date.from(Instant.now()));

        Penalties penalties = Penalties.builder()
                .warnings(1)
                .ban(ban)
                .build();

        // Detalles de pagos
        Subscription subscription = Subscription.builder()
                .stripeSubcriptionId("3232kgr45Ad9")
                .status("Activa")
                .type("Monitor")
                .selfRenewal(true)
                .expirationDate(Date.from(Instant.now().plus(30, DAYS)))
                .build();

        BillingDetails billingDetails = BillingDetails.builder()
                .stripeCustomerId("123f45FD7j231")
                .subscription(subscription)
                .build();


        User user = User.builder()
                .name("Sergio Ruiz")
                .profileImg("https://stanforddaily.com/wp-content/uploads/2025/01/IMG_1172.jpg")
                .email("sergioreactpro@gmail.com")
                .password("Admin1234!!")
                .deportiveProfile(dp)
                .penalties(penalties)
                .billingDetails(billingDetails)
                .createdAt(LocalDate.now())
                .bod(LocalDate.of(1924, 1, 17))
                .rol("USER")
                .QRCode("QR123456789")
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

    @Test
    void shouldFindUserByEmail() {

        User result = userRepository.findByEmail("sergioreactpro@gmail.com")
                .orElseThrow(() -> new AssertionError("Usuario no encontrado por email"));

        assertEquals("Sergio Ruiz", result.getName());
    }

    @Test
    void shouldReturnTrueIfEmailExists() {

        boolean exists = userRepository.existsByEmail("sergioreactpro@gmail.com");

        assertTrue(exists);
    }

    @Test
    void shouldFindUserByQRCode() {

        User result = userRepository.findByQRCode("QR123456789")
                .orElseThrow(() -> new AssertionError("Usuario no encontrado por QRCode"));

        assertEquals("sergioreactpro@gmail.com", result.getEmail());
    }

    @Test
    void shouldFindUsersByRole() {

        List<User> users = userRepository.findByRol("USER");

        assertFalse(users.isEmpty());
        assertEquals("USER", users.getFirst().getRol());
    }

    @Test
    void shouldFindUsersByNameContainingIgnoreCase() {

        List<User> users = userRepository.findByNameContainingIgnoreCase("Sergio");

        assertFalse(users.isEmpty());
        assertEquals("Sergio Ruiz", users.getFirst().getName());
    }
}
