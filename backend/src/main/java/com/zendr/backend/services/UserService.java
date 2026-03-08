package com.zendr.backend.services;

import com.zendr.backend.internal.user.model.*;
import com.zendr.backend.internal.user.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // Creacion
    User save(User user);

    // Busquedas
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByDisciplineId(String disciplineId);
    Optional<User> findByQRCode(String qrCode);

    // Actualizar
    Optional<User> updateUserDTO(String id, UserUpdateDTO dto);
    Optional<User> updateEmail(String id, String email);
    Optional<User> updatePassword(String id, String password);
    Optional<User> updateUsername(String id, String username);
    Optional<User> updateRole(String id, UserRole role);
    Optional<User> updateDeportiveProfile(String id, DeportiveProfile profile);
    Optional<User> updateFavDisciplines(String id, List<FavDisciplines> favDisciplines);
    Optional<User> updateBillingDetails(String id, BillingDetails details);
    Optional<User> updateSubscription(String id, Subscription sub);

    // Eliminar
    void deleteById(String id);

    // Checks
    boolean existsByEmail(String email);
    boolean existsById(String id);
    boolean existsByUsername(String username);

    // Penalizaciones
    Optional<User> applyPenalty(String id);
    Optional<User> applyBanTrue(String id);
    Optional<User> resetPenalties(String id);
    Optional<User> applyBanFalse(String id);

    // Subscripcion
    Optional<User> applyActiveSubscription(String id);
    Optional<User> applySuspendedSuscription(String id);
    Optional<User> applyBannedSubscription(String id);
    Optional<User> toggleSelfRenewal(String id);

    // TODO: Aplicar logica de transacciones

}

