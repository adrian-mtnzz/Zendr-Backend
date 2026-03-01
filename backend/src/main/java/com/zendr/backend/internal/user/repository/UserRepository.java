package com.zendr.backend.internal.user.repository;

import com.zendr.backend.internal.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByNameContainingIgnoreCase(String username);
    List<User> findByRol(String rol);
    List<User> findByDeportiveDisciplinesContains(String discipine);
    Optional<User> findByEmail(String email);
    Optional<User> findByQRCode(String QRCode);
    boolean existsByEmail(String email);
}
