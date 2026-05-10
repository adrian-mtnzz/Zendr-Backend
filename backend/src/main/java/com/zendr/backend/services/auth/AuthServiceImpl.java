package com.zendr.backend.services.auth;

import com.zendr.backend.internal.device.model.Device;
import com.zendr.backend.internal.token.model.AuthRequest;
import com.zendr.backend.internal.token.model.RegisterRequest;
import com.zendr.backend.internal.token.model.Token;
import com.zendr.backend.internal.token.model.TokenResponse;
import com.zendr.backend.internal.token.repository.TokenRepository;
import com.zendr.backend.internal.user.model.User;
import com.zendr.backend.internal.user.repository.UserRepository;
import com.zendr.backend.services.device.DeviceService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {
    private final UserRepository repository;
    private final DeviceService deviceService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    
    public TokenResponse register(final RegisterRequest request) {
        final User user = User.builder().build();
        // TODO meter en el RegisterRequest todos los campos necesarios de user y hacer validaciones
        // como en UserService save();
        // Meter tambien los necesarios en device
        
        final User savedUser = repository.save(user);
        
        final Device savedDevice = deviceService.save(
                savedUser.getId(),
                request.platform(),
                request.deviceModel(),
                request.ipAddress()
        );
        
        final String jwtToken = jwtService.generateToken(savedUser);
        final String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        saveUserToken(savedUser, refreshToken, savedDevice.getId());
        return new TokenResponse(jwtToken, refreshToken);
    }
    
    
    public TokenResponse authenticate(final AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        final User user = repository.findByEmail(request.email()).orElseThrow(
                () -> new IllegalArgumentException("Usuario no encontrado")
        );
        
        final String accessToken = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user.getId());
        
        return new TokenResponse(accessToken, refreshToken);
    }
    
    
    private void saveUserToken(User user, String jwtToken, String deviceId) {
        final Token token = Token.builder()
                .userId(user.getId())
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtExpiration))
                .isExpired(false)
                .isRevoked(false)
                .deviceId(deviceId)
                .build();
        
        tokenRepository.save(token);
    }
    
    private void revokeAllUserTokens(final String userId) {
        final List<Token> validUserTokens = tokenRepository.findByUserIdAndRevokedFalseAndExpiresAtAfter(userId, Instant.now());
        
        if(!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            
            tokenRepository.saveAll(validUserTokens);
        }
    }
    
    public TokenResponse refreshToken(@NotNull final String authentication) {
        if(authentication == null || !authentication.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Header de autenticación inválido");
        }
        
        final String refreshToken = authentication.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);
        
        if (userEmail == null) return null;
        
        final User user = repository.findByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException("No se ha encontrado el usuario")
        );
        
        final boolean isTokenValid = jwtService.isTokenValid(refreshToken, user);
        if (!isTokenValid) return null;
        
        final String deviceId = deviceService.findByUserId(user.getId()).getId();
        
        final String accessToken = jwtService.generateRefreshToken(user);
        
        revokeAllUserTokens(user.getId());
        
        return new TokenResponse(accessToken, refreshToken);
    }
}
