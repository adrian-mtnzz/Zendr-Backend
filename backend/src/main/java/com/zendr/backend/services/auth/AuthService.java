package com.zendr.backend.services.auth;

import com.zendr.backend.internal.token.model.AuthRequest;
import com.zendr.backend.internal.token.model.RegisterRequest;
import com.zendr.backend.internal.token.model.TokenResponse;

public interface AuthService {
    TokenResponse register(final RegisterRequest request);
    TokenResponse authenticate(final AuthRequest request);
    TokenResponse refreshToken(final String authentication);
}
