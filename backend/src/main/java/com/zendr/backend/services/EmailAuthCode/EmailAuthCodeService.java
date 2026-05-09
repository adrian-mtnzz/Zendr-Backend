package com.zendr.backend.services.EmailAuthCode;

import com.zendr.backend.internal.emailAuthCode.model.EmailAuthCode;

import java.time.Instant;
import java.util.Optional;

public interface EmailAuthCodeService {
    Optional<Instant> generateCode(String email);
    boolean validateCode(String email, String code);
    void revokeCode(String email);
}
