package com.zendr.backend.services.auth;

import com.zendr.backend.internal.token.model.AuthRequest;
import com.zendr.backend.internal.token.model.RegisterRequest;
import com.zendr.backend.internal.token.model.TokenResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    TokenResponse register(final RegisterRequest request, MultipartFile file);
    TokenResponse authenticate(final AuthRequest request);
    TokenResponse refreshToken(final String authentication);
}
