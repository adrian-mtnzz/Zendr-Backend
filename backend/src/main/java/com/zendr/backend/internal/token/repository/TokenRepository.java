package com.zendr.backend.internal.token.repository;

import com.zendr.backend.internal.token.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    
    Optional<Token> findByToken(String token);
    List<Token> findByUserIdAndRevokedFalseAndExpiresAtAfter(String userId, Instant now);
}