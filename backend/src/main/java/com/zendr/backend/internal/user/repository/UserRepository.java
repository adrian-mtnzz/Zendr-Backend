package com.zendr.backend.internal.user.repository;

import com.zendr.backend.internal.user.model.User;
import com.zendr.backend.internal.user.model.enums.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByNameContainingIgnoreCase(String username);
    List<User> findByRole(UserRole rol);
    List<User> findByDeportiveProfileContains(String discipineId);
    Optional<User> findByEmail(String email);
    Optional<User> findByQRCode(String QRCode);
    boolean existsByEmail(String email);
}

