package com.zendr.backend.services.user;

import com.zendr.backend.internal.user.model.*;
import com.zendr.backend.internal.user.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // Creacion
    User save(User user);

    // Busquedas DTO
    List<UserDTO> findAll();
    Optional<UserDTO> findById(String id);
    Optional<User> findByIdRaw(String id);
    Optional<UserDTO> findByUsername(String username);
    Optional<UserDTO> findByEmail(String email);
    List<UserDTO> findByRole(UserRole role);
    List<UserDTO> findByDisciplineId(String disciplineId);
    Optional<UserDTO> findByQRCode(String qrCode);

    // Busquedas Parciales
    Optional<String> getEmail(String id);
    Optional<String> getRole(String id);
    Optional<String> getQRCode(String id);
    Optional<DeportiveProfile> getDeportiveProfile(String id);
    Optional<Penalties> getPenalties(String id);
    Optional<BillingDetails> getBillingDetails(String id);

    // Actualizar
    Optional<UserDTO> updateUserFromDTO(String id, UserDTO dto);
    Optional<String> updateEmail(String id, String email);
    boolean updatePassword(String code, String email, String password);
    Optional<String> updateUsername(String id, String username);
    Optional<String> updateRole(String id, UserRole role);
    Optional<DeportiveProfile> updateDeportiveProfile(String id, DeportiveProfile profile);
    Optional<DeportiveProfile> updateFavDisciplines(String id, List<FavDisciplines> favDisciplines);
    Optional<BillingDetails> updateBillingDetails(String id, BillingDetails details);
    Optional<Subscription> updateSubscription(String id, Subscription sub);

    // Eliminar
    void deleteById(String id);

    // Checks
    boolean checkPassword(String id, String password);
    boolean existsByEmail(String email);
    boolean existsById(String id);
    boolean existsByUsername(String username);

    // Penalizaciones
    Optional<Penalties> applyPenalty(String id);
    Optional<Penalties> applyBanTrue(String id);
    Optional<Penalties> resetPenalties(String id);
    Optional<Penalties> applyBanFalse(String id);

    // Subscripcion
    Optional<Subscription> applyActiveSubscription(String id);
    Optional<Subscription> applySuspendedSuscription(String id);
    Optional<Subscription> applyBannedSubscription(String id);
    Optional<Subscription> toggleSelfRenewal(String id);

    // TODO: Aplicar logica de transacciones

}

