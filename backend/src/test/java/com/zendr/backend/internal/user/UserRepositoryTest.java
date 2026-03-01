package com.zendr.backend.internal.user;

import com.zendr.backend.internal.user.model.*;
import com.zendr.backend.internal.user.repository.UserRepository;
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

import javax.swing.text.html.Option;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@DataMongoTest
@ActiveProfiles("dev")
@Testcontainers
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;

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


        user = User.builder()
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
        Optional<User> user = userRepository.findByEmail("sergioreactpro@gmail.com");
        Optional<User> result = userRepository.findById(user.get().getId());

        assert(result).isPresent();
        assert(result.get().getName()).equals("Sergio Ruiz");
    }

    @Test
    void shouldFindUserByEmail() {

        Optional<User> result = userRepository.findByEmail("sergioreactpro@gmail.com");

        assert(result).isPresent();
        assert(result.get().getName()).equals("Sergio Ruiz");
    }

    @Test
    void shouldReturnTrueIfEmailExists() {

        boolean exists = userRepository.existsByEmail("sergioreactpro@gmail.com");

        assert(exists);
    }

    @Test
    void shouldFindUserByQRCode() {

        Optional<User> result = userRepository.findByQRCode("QR123456789");

        assert(result).isPresent();
        assert(result.get().getEmail()).equals("sergioreactpro@gmail.com");
    }

    @Test
    void shouldFindUsersByRole() {

        List<User> users = userRepository.findByRol("USER");

        assert !(users).isEmpty();
        assert(users.getFirst().getRol()).equals("USER");
    }

    @Test
    void shouldFindUsersByNameContainingIgnoreCase() {

        List<User> users = userRepository.findByNameContainingIgnoreCase("Sergio");

        assert !(users).isEmpty();
        assert(users.getFirst().getName()).equals("Sergio Ruiz");
    }
}
