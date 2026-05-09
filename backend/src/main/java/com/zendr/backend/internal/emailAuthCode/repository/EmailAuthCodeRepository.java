package com.zendr.backend.internal.emailAuthCode.repository;

import com.zendr.backend.internal.emailAuthCode.model.EmailAuthCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailAuthCodeRepository extends MongoRepository<EmailAuthCode, String> {
    
    Optional<EmailAuthCode> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}