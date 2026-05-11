package com.zendr.backend.services.emailAuthCode;

import com.zendr.backend.internal.emailAuthCode.model.EmailAuthCode;
import com.zendr.backend.internal.emailAuthCode.repository.EmailAuthCodeRepository;
import com.zendr.backend.internal.user.repository.UserRepository;
import com.zendr.backend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailAuthCodeServiceImpl implements EmailAuthCodeService {
    
    private final EmailAuthCodeRepository repository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    
    @Override
    public Optional<Instant> generateCode(String email) {
        
        repository.deleteByEmail(email);
        
        String code = generateRandomCode();
        
        EmailAuthCode authCode = EmailAuthCode.builder()
                .email(email)
                .code(code)
                .build();
        
        repository.save(authCode);
        emailService.sendAuthCode(email, code);
        
        return Optional.of(authCode.getExpiresAt());
    }
    
    @Override
    public boolean validateCode(String email, String code) {
        
        Optional<EmailAuthCode> optionalAuth = repository.findByEmail(email);
        
        if (optionalAuth.isEmpty()) {
            return false;
        }
        
        EmailAuthCode auth = optionalAuth.get();
        
        if (auth.isRevoked() || auth.getExpiresAt().isBefore(Instant.now())) {
            return false;
        }
        
        if (auth.getCode().equals(code)) {
            auth.revoke();
            repository.save(auth);
            return true;
        }
        
        auth.increaseAttempts();
        repository.save(auth);
        return false;
    }
    
    
    @Override
    public void revokeCode(String email) {
        
        repository.findByEmail(email)
                .ifPresent(auth -> {
                    auth.revoke();
                    repository.save(auth);
                });
    }
    
    private String generateRandomCode() {
        
        return String.format("%06d",
                ThreadLocalRandom.current().nextInt(0, 1_000_000));
    }
}

