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
    Optional<User> findByUsername(String username);
    List<User> findByRole(UserRole rol);
    List<User> findByDeportiveProfile_FavDisciplines_DisciplineId(String disciplineId);
    Optional<User> findByEmail(String email);
    Optional<User> findByQRCode(String qrCode);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}


