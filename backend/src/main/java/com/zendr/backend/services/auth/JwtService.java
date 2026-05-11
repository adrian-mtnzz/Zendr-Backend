package com.zendr.backend.services.auth;

import com.zendr.backend.internal.user.model.User;
import org.springframework.security.core.userdetails.UserDetails;


public interface JwtService {
    String extractUsername(String token);
    String generateToken( User user);
    String generateRefreshToken(User user);
    boolean isTokenValid(String token, String email);
}
