package com.zendr.backend.services;

import com.zendr.backend.internal.user.model.*;
import com.zendr.backend.internal.user.model.enums.SubscriptionStatus;
import com.zendr.backend.internal.user.model.enums.UserRole;
import com.zendr.backend.internal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    public User save(User user) {
        return repo.save(user);
    }

    @Override
    public List<User> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        return repo.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return repo.findByRole(role);
    }

    @Override
    public List<User> findByDisciplineId(String disciplineId) {
        return repo.findByDeportiveProfile_FavDisciplines_DisciplineId(disciplineId);
    }

    @Override
    public Optional<User> findByQRCode(String qrCode) {
        return repo.findByQRCode(qrCode);
    }

    @Override
    public Optional<User> updateUserDTO(String id, UserUpdateDTO dto) {
        return repo.findById(id).map(user -> {

            if (dto.getName() != null) user.setName(dto.getName());
            if (dto.getSurname() != null) user.setSurname(dto.getSurname());
            if (dto.getProfileImg() != null) user.setProfileImg(dto.getProfileImg());
            if (dto.getBod() != null) user.setBod(dto.getBod());

            return repo.save(user);
        });
    }

    @Override
    public Optional<User> updateEmail(String id, String email) {

        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("El email no es válido");
        }

        return repo.findById(id).map(user -> {

            if (repo.existsByEmail(email)) {
                throw new RuntimeException("El email '" + email + "' ya está en uso");
            }
            user.setEmail(email);
            return repo.save(user);
        });
    }

    @Override
    public Optional<User> updatePassword(String id, String password) {

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        return repo.findById(id).map(user -> {

            if (user.getPassword().equals(password)) return user;
            user.setPassword(password);
            return repo.save(user);
        });
    }

    @Override
    public Optional<User> updateUsername(String id, String username) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }

        return repo.findById(id).map(user -> {

            if (repo.existsByUsername(username)) {
                throw new RuntimeException("El username '" + username + "' ya está en uso");
            }
            user.setUsername(username);
            return repo.save(user);
        });
    }

    @Override
    public Optional<User> updateRole(String id, UserRole role) {

        if (role == null) {
            throw new IllegalArgumentException("El rol no puede estar vacío");
        }

        return repo.findById(id).map(user -> {

            if (user.getRole() == role) return user;

            user.setRole(role);
            return repo.save(user);
        });
    }

    @Override
    public Optional<User> updateDeportiveProfile(String id, DeportiveProfile profile) {

        Objects.requireNonNull(profile, "El perfil deportivo no puede ser nulo");

        return repo.findById(id).map(user -> {
            user.setDeportiveProfile(profile);
            return repo.save(user);
        });
    }

    @Override
    public Optional<User> updateFavDisciplines(String id, List<FavDisciplines> favDisciplines) {
        Objects.requireNonNull(favDisciplines, "El objeto de disciplinas favoritas no puede ser nulo");

        Objects.requireNonNull(favDisciplines, "La lista de disciplinas no puede ser nula");

        return repo.findById(id).map(user -> {
            // 2. Asegurar que existe el perfil deportivo
            if (user.getDeportiveProfile() == null) {
                user.setDeportiveProfile(new DeportiveProfile());
            }

            // 3. Reemplazo total de la lista
            user.getDeportiveProfile().setFavDisciplines(favDisciplines);

            return repo.save(user);
        });
    }

    @Override
    public Optional<User> updateBillingDetails(String id, BillingDetails details) {

        Objects.requireNonNull(details, "Los detalles de facturación no pueden ser nulos");

        return repo.findById(id).map(user -> {
            user.setBillingDetails(details);
            return repo.save(user);
        });
    }

    @Override
    public Optional<User> updateSubscription(String id, Subscription sub) {
        Objects.requireNonNull(sub, "La suscripción no puede ser nula");

        return repo.findById(id).map(user -> {

            if (user.getBillingDetails() != null) {
                user.getBillingDetails().setSubscription(sub);

            } else throw new IllegalStateException("No se puede añadir una suscripción si el usuario no tiene stripeCustomerId");

            return repo.save(user);
        });
    }

    @Override
    public void deleteById(String id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Usuario no encontrado con ID: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    @Override
    public boolean existsById(String id) {
        return repo.existsById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repo.existsByUsername(username);
    }

    @Override
    public Optional<User> applyPenalty(String id) {

        return repo.findById(id).map(user -> {

            int currentWarnings = user.getPenalties().getWarnings();
            user.getPenalties().setWarnings(currentWarnings + 1);

            if (user.getPenalties().getWarnings() >= 3) {
                this.applyBanTrue(id);
            }

            return repo.save(user);
        });
    }

    @Override
    public Optional<User> applyBanTrue(String id) {

        return repo.findById(id).map(user -> {

            if (user.getPenalties() != null && !user.getPenalties().getBan().isBanned()) {
                user.getPenalties().getBan().setBanned(true);
                user.getPenalties().getBan().setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
            }
            return repo.save(user);
        });
    }

    @Override
    public Optional<User> applyBanFalse(String id) {

        return repo.findById(id).map(user -> {

            if (user.getPenalties() != null && user.getPenalties().getBan().isBanned()) {
                user.getPenalties().getBan().setBanned(false);
                user.getPenalties().getBan().setExpiresAt(null);
            }
            return repo.save(user);
        });
    }

    @Override
    public Optional<User> resetPenalties(String id) {

        return repo.findById(id).map(user -> {

            user.setPenalties(Penalties.builder()
                    .warnings(0)
                    .ban(BanStatus.builder().isBanned(false).expiresAt(null).build())
                    .build());

            return repo.save(user);
        });
    }


    @Override
    public Optional<User> applyActiveSubscription(String id) {

        return repo.findById(id).map(user -> {

            if (user.getBillingDetails() != null && user.getBillingDetails().getSubscription() != null) {
                Subscription sub = user.getBillingDetails().getSubscription();

                sub.setStatus(SubscriptionStatus.ACTIVE);

                sub.setExpirationDate(Instant.now().plus(30, ChronoUnit.DAYS));

                return repo.save(user);
            }
            throw new IllegalStateException("El usuario no tiene una suscripción configurada");
        });
    }

    @Override
    public Optional<User> applySuspendedSuscription(String id) {
    // TODO: Cuando se aplique logica de transacciones aplicar intento de renovacion si selfRenewal es true
        return repo.findById(id).map(user -> {

            if (user.getBillingDetails() != null && user.getBillingDetails().getSubscription() != null) {
                user.getBillingDetails().getSubscription().setStatus(SubscriptionStatus.SUSPENDED);

                return repo.save(user);
            }
            return user;
        });
    }

    @Override
    public Optional<User> applyBannedSubscription(String id) {

        return repo.findById(id).map(user -> {

            if (user.getBillingDetails() != null && user.getBillingDetails().getSubscription() != null) {
                Subscription sub = user.getBillingDetails().getSubscription();

                sub.setStatus(SubscriptionStatus.BANNED);

                sub.setSelfRenewal(false);
                sub.setExpirationDate(Instant.now());

                return repo.save(user);
            }

            // Si no tiene suscripción, no hay nada que banear en billingDetails
            return user;
        });
    }

    @Override
    public Optional<User> toggleSelfRenewal(String id) {

        return repo.findById(id).map(user -> {

            if (user.getBillingDetails() != null && user.getBillingDetails().getSubscription() != null) {
                Subscription sub = user.getBillingDetails().getSubscription();

                sub.setSelfRenewal(!sub.isSelfRenewal());
            }
            return repo.save(user);
        });
    }

}
