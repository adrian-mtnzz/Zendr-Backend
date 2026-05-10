package com.zendr.backend.services.auth;

import com.zendr.backend.internal.user.model.User;

import javax.crypto.SecretKey;
import java.util.Date;

public interface JwtService {
    String extractUsername(String token);
    String generateToken( User user);
    String generateRefreshToken(User user);
    boolean isTokenValid(String token, User user);
}
