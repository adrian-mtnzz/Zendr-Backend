package com.zendr.backend.services.user;

import com.zendr.backend.internal.discipline.repository.DisciplineRepository;
import com.zendr.backend.internal.user.model.*;
import com.zendr.backend.internal.user.model.enums.SubscriptionStatus;
import com.zendr.backend.internal.user.model.enums.UserRole;
import com.zendr.backend.internal.user.repository.UserRepository;
import com.zendr.backend.services.emailAuthCode.EmailAuthCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final DisciplineRepository disciplineRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthCodeService authCodeService;

    @Override
    public User save(User user) {
        
        if (user.getUsername() == null) throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        if (existsByUsername(user.getUsername())) throw new IllegalArgumentException("El nombre de usuario ya existe");
        
        if (user.getEmail() == null) throw new IllegalArgumentException("El email no puede estar vacío");
        if (existsByEmail(user.getEmail())) throw new IllegalArgumentException("El email de usuario ya existe");
        
        if (user.getDeportiveProfile() != null && user.getDeportiveProfile().getFavDisciplines() != null) {
            boolean isFavDisciplinesValid = user.getDeportiveProfile().getFavDisciplines().stream().allMatch(
                    discipline -> disciplineRepository.existsById(discipline.getDisciplineId())
            );
            if (!isFavDisciplinesValid) throw new IllegalArgumentException("Las disiciplinas no son válidas");
        }
        
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        user.setRole(UserRole.USER);
        
        return repo.save(user);
    }

    @Override
    public List<UserDTO> findAll() {

        return repo.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<UserDTO> findById(String id) {

        return repo.findById(id).map(UserMapper::toDTO);
    }

    @Override
    public Optional<User> findByIdRaw(String id) {

        return repo.findById(id);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {

        return repo.findByUsername(username).map(UserMapper::toDTO);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {

        return repo.findByEmail(email).map(UserMapper::toDTO);
    }

    @Override
    public List<UserDTO> findByRole(UserRole role) {

        return repo.findByRole(role)
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @Override
    public List<UserDTO> findByDisciplineId(String disciplineId) {

        return repo.findByDeportiveProfile_FavDisciplines_DisciplineId(disciplineId)
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<UserDTO> findByQRCode(String qrCode) {

        return repo.findByQRCode(qrCode)
                .map(UserMapper::toDTO);
    }

    @Override
    public Optional<String> getEmail(String id) {

        return repo.findById(id)
                .map(User::getEmail);
    }

    @Override
    public Optional<String> getRole(String id) {

        return repo.findById(id)
                .map(user -> user.getRole().name());
    }

    @Override
    public String getQRCode(String id) {

        String QRCode = repo.findById(id)
                .map(User::getQRCode)
                .orElseThrow(
                    () -> new IllegalArgumentException("No se ha encontrado el código QR de este usuario"));
    
        return QRCode;
    }

    @Override
    public Optional<DeportiveProfile> getDeportiveProfile(String id) {

        return repo.findById(id)
                .map(User::getDeportiveProfile);
    }

    @Override
    public Optional<Penalties> getPenalties(String id) {

        return repo.findById(id)
                .map(User::getPenalties);
    }

    @Override
    public Optional<BillingDetails> getBillingDetails(String id) {

        return repo.findById(id)
                .map(User::getBillingDetails);
    }

    @Override
    public Optional<UserDTO> updateUserFromDTO(String id, UserDTO dto) {

        return repo.findById(id).map(user -> {

            if (dto.getUsername() != null && !repo.existsByUsername(dto.getUsername())) user.setUsername(dto.getUsername());
            if (dto.getName() != null) user.setName(dto.getName());
            if (dto.getSurname() != null) user.setSurname(dto.getSurname());
            if (dto.getProfileImg() != null) user.setProfileImg(dto.getProfileImg());
            return UserMapper.toDTO(repo.save(user));
        });
    }

    @Override
    public Optional<String> updateEmail(String id, String email) {

        if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("El email no es válido");
        }

        return repo.findById(id).map(user -> {
            if (!email.equals(user.getEmail()) && repo.existsByEmail(email)) {
                throw new RuntimeException("El email '" + email + "' ya está en uso");
            }

            user.setEmail(email);
            repo.save(user);

            return user.getEmail();
        });
    }

    @Override
    @Transactional
    public boolean updatePassword(String code, String email, String password) {
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (code == null || code.trim().isEmpty() || code.length() != 6) {
            throw new IllegalArgumentException("El codigo no es válido");
        }
        
        Optional<User> optionalUser = repo.findByEmail(email);
        
        if (optionalUser.isEmpty()) return false;
        boolean validCode = authCodeService.validateCode(email, code);
        
        if (!validCode) return false;
        
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(password));
        
        repo.save(user);
        return true;
    }

    @Override
    public Optional<String> updateUsername(String id, String username) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }

        return repo.findById(id).map(user -> {

            if (!username.equals(user.getUsername()) && repo.existsByUsername(username)) {
                throw new RuntimeException("El username '" + username + "' ya está en uso");
            }

            user.setUsername(username);
            repo.save(user);

            return user.getUsername();
        });
    }

    @Override
    public Optional<String> updateRole(String id, UserRole role) {

        if (role == null) {
            throw new IllegalArgumentException("El rol no puede estar vacío");
        }

        return repo.findById(id).map(user -> {

            if (user.getRole() == role) return user.getRole().getDescription();

            user.setRole(role);
            repo.save(user);

            return user.getRole().getDescription();
        });
    }

    @Override
    public Optional<DeportiveProfile> updateDeportiveProfile(String id, DeportiveProfile profile) {

        Objects.requireNonNull(profile, "El perfil deportivo no puede ser nulo");
        if (profile.getFavDisciplines() != null) {
            boolean isFavDisciplinesValid = profile.getFavDisciplines().stream().allMatch(
                    discipline -> disciplineRepository.existsById(discipline.getDisciplineId())
            
            );
            if (!isFavDisciplinesValid) throw new IllegalArgumentException("Las disiciplinas no son válidas");
        }
        return repo.findById(id).map(user -> {

            user.setDeportiveProfile(profile);
            repo.save(user);

            return user.getDeportiveProfile();
        });
    }

    @Override
    public Optional<DeportiveProfile> updateFavDisciplines(String id, List<FavDisciplines> favDisciplines) {

        Objects.requireNonNull(favDisciplines, "La lista de disciplinas favoritas no puede ser nula");
        
        boolean isFavDisciplinesValid = favDisciplines.stream().allMatch(
                discipline -> disciplineRepository.existsById(discipline.getDisciplineId())
        );
        if (!isFavDisciplinesValid) throw new IllegalArgumentException("Las disiciplinas no son válidas");
        
        return repo.findById(id).map(user -> {

            if (user.getDeportiveProfile() == null) {
                
                user.setDeportiveProfile(new DeportiveProfile());
            }
            

            user.getDeportiveProfile().setFavDisciplines(favDisciplines);
            repo.save(user);

            return user.getDeportiveProfile();
        });
    }

    @Override
    public Optional<BillingDetails> updateBillingDetails(String id, BillingDetails details) {

        Objects.requireNonNull(details, "Los detalles de facturación no pueden ser nulos");

        return repo.findById(id).map(user -> {

            user.setBillingDetails(details);
            repo.save(user);

            return user.getBillingDetails();
        });
    }

    @Override
    public Optional<Subscription> updateSubscription(String id, Subscription sub) {

        Objects.requireNonNull(sub, "La suscripción no puede ser nula");

        return repo.findById(id).map(user -> {

            if (user.getBillingDetails() != null) {
                user.getBillingDetails().setSubscription(sub);

            } else throw new IllegalStateException("No se puede añadir una suscripción si el usuario no tiene stripeCustomerId");

            repo.save(user);
            return user.getBillingDetails().getSubscription();
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
    public boolean checkPassword(String id, String password) {

        return repo.findById(id)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
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
    public Optional<Penalties> applyPenalty(String id) {

        return repo.findById(id).map(user -> {

            if (user.getPenalties() == null) {
                user.setPenalties(Penalties.builder()
                        .warnings(0)
                        .ban(new BanStatus(false,null))
                        .build());
            }

            Penalties penalties = user.getPenalties();
            penalties.setWarnings(penalties.getWarnings() + 1);

            if (penalties.getWarnings() >= 3) {
                this.applyBanTrue(id);
                penalties.setWarnings(0);
            }

            repo.save(user);
            return user.getPenalties();
        });
    }

    @Override
    public Optional<Penalties> applyBanTrue(String id) {

        return repo.findById(id).map(user -> {

            if (user.getPenalties() != null && !user.getPenalties().getBan().isBanned()) {
                user.getPenalties().getBan().setBanned(true);
                user.getPenalties().getBan().setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
            }

            repo.save(user);
            return user.getPenalties();
        });
    }

    @Override
    public Optional<Penalties> applyBanFalse(String id) {

        return repo.findById(id).map(user -> {

            if (user.getPenalties() != null && user.getPenalties().getBan().isBanned()) {
                user.getPenalties().getBan().setBanned(false);
                user.getPenalties().getBan().setExpiresAt(null);
            }

            repo.save(user);
            return user.getPenalties();
        });
    }

    @Override
    public Optional<Penalties> resetPenalties(String id) {

        return repo.findById(id).map(user -> {

            user.setPenalties(Penalties.builder()
                    .warnings(0)
                    .ban(BanStatus.builder().isBanned(false).expiresAt(null).build())
                    .build());

            repo.save(user);
            return user.getPenalties();
        });
    }


    @Override
    public Optional<Subscription> applyActiveSubscription(String id) {

        return repo.findById(id).map(user -> {

            if (user.getBillingDetails() != null && user.getBillingDetails().getSubscription() != null) {
                Subscription sub = user.getBillingDetails().getSubscription();

                sub.setStatus(SubscriptionStatus.ACTIVE);

                sub.setExpirationDate(Instant.now().plus(30, ChronoUnit.DAYS));

                repo.save(user);
                return user.getBillingDetails().getSubscription();
            }
            throw new IllegalStateException("El usuario no tiene una suscripción configurada");
        });
    }

    @Override
    public Optional<Subscription> applySuspendedSuscription(String id) {
    // TODO: Cuando se aplique logica de transacciones aplicar intento de renovacion si selfRenewal es true
        return repo.findById(id).flatMap(user -> {

            if (user.getBillingDetails() != null && user.getBillingDetails().getSubscription() != null) {
                Subscription sub = user.getBillingDetails().getSubscription();

                sub.setStatus(SubscriptionStatus.SUSPENDED);

                repo.save(user);
                return Optional.of(user.getBillingDetails().getSubscription());
            }
            return Optional.empty();
        });
    }

    @Override
    public Optional<Subscription> applyBannedSubscription(String id) {

        return repo.findById(id).flatMap(user -> {

            if (user.getBillingDetails() != null && user.getBillingDetails().getSubscription() != null) {
                Subscription sub = user.getBillingDetails().getSubscription();

                sub.setStatus(SubscriptionStatus.BANNED);

                sub.setSelfRenewal(false);
                sub.setExpirationDate(Instant.now());

                repo.save(user);
                return Optional.of(sub);
            }

            return Optional.empty();
        });
    }

    @Override
    public Optional<Subscription> toggleSelfRenewal(String id) {

        return repo.findById(id).map(user -> {

            if (user.getBillingDetails() != null && user.getBillingDetails().getSubscription() != null) {
                Subscription sub = user.getBillingDetails().getSubscription();

                sub.setSelfRenewal(!sub.isSelfRenewal());
            }

            repo.save(user);
            return user.getBillingDetails().getSubscription();
        });
    }

}
