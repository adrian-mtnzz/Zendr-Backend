package com.zendr.backend.services.auth;

import com.zendr.backend.internal.device.model.Device;
import com.zendr.backend.internal.discipline.repository.DisciplineRepository;
import com.zendr.backend.internal.token.model.AuthRequest;
import com.zendr.backend.internal.token.model.RegisterRequest;
import com.zendr.backend.internal.token.model.Token;
import com.zendr.backend.internal.token.model.TokenResponse;
import com.zendr.backend.internal.token.repository.TokenRepository;
import com.zendr.backend.internal.user.model.User;
import com.zendr.backend.internal.user.model.enums.UserRole;
import com.zendr.backend.internal.user.repository.UserRepository;
import com.zendr.backend.services.device.DeviceService;
import com.zendr.backend.services.emailAuthCode.EmailAuthCodeService;
import com.zendr.backend.services.storage.BucketService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository repository;
    private final DisciplineRepository disciplineRepository;
    private final DeviceService deviceService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final EmailAuthCodeService authCodeService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    
    private final BucketService bucketService;
    
    
    public TokenResponse register(final RegisterRequest request, MultipartFile file) {
        
        
        if (request.username() == null || repository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El nombre de usuario no es válido");
        }
        
        if (request.email() == null || request.code() == null) {
            throw new IllegalArgumentException("No se ha podido verificar el código");
        }
        
        if (request.password() == null) throw new IllegalArgumentException("La contraseña no puede estar vacía");
        
        String password = request.password();
        
        if (password.startsWith("$2a$")) {
                password = passwordEncoder.encode(request.password());
        }
        
        if (request.deportiveProfile() != null && request.deportiveProfile().getFavDisciplines() != null) {
            boolean isFavDisciplinesValid = request.deportiveProfile().getFavDisciplines().stream().allMatch(
                    discipline -> disciplineRepository.existsById(discipline.getDisciplineId())
            );
            if (!isFavDisciplinesValid) throw new IllegalArgumentException("Las disiciplinas no son válidas");
        }
        
        if (!authCodeService.validateCode(request.email(), request.code())) {
            throw new IllegalArgumentException("Código incorrecto");
        }
        
        String profileImgUrl = "";
        try {
            // SUBIR IMAGEN
            profileImgUrl = Objects.requireNonNull(file.getContentType()).startsWith("image/")
                    ? bucketService.uploadFile(file, "users")
                    : "users/dbede52b-801a-49ce-8765-535bc02fad1f.png"; // Fallback imagen para eventos
        
        } catch (IOException e) {
            profileImgUrl = "users/dbede52b-801a-49ce-8765-535bc02fad1f.png";
        }
        
        User user = User.builder()
                .username(request.username())
                .name(request.name())
                .surname(request.surname())
                .email(request.email())
                .password(passwordEncoder.encode(password))
                .deportiveProfile(request.deportiveProfile())
                .dob(request.dob())
                .role(UserRole.USER)
                .createdAt(LocalDate.now())
                .build();
        
        final User savedUser = repository.save(user);
        
        final Device savedDevice = deviceService.save(
                savedUser.getId(),
                request.platform(),
                request.deviceModel(),
                request.ipAddress()
        );
        
        final String accessToken = jwtService.generateToken(savedUser);
        final String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        saveUserToken(savedUser, refreshToken, savedDevice.getId());
        return new TokenResponse(accessToken, refreshToken, user.getId(), savedDevice.getId(), savedUser.getRole().getDescription());
    }
    
    
    public TokenResponse authenticate(final AuthRequest request) {
        
        System.out.println(repository.findByEmail(request.email()));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        final User user = repository.findByEmail(request.email()).orElseThrow(
                () -> new IllegalArgumentException("Usuario no encontrado")
        );
        
        String deviceId = null;
        
            if (request.deviceId() != null && deviceService.findById(request.deviceId()) != null) {
                deviceId = request.deviceId();
            }
            
            else if (
                request.deviceId() == null &&
                request.deviceModel() != null &&
                request.platform() != null &&
                request.ipAddress() != null
            ) {
                final Device savedDevice = deviceService.save(
                        user.getId(),
                        request.platform(),
                        request.deviceModel(),
                        request.ipAddress()
                );
                deviceId = savedDevice.getId();
                
            } else throw new IllegalArgumentException("Los datos del dispositivo no son válidos");
        
        final String accessToken = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        
        revokeAllUserTokens(user.getId());
        saveUserToken(user, refreshToken, deviceId);
        return new TokenResponse(accessToken, refreshToken, user.getId(), deviceId, user.getRole().getDescription());
    }
    
    
    private void saveUserToken(User user, String jwtToken, String deviceId) {
        final Token token = Token.builder()
                .userId(user.getId())
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtExpiration))
                .expired(false)
                .revoked(false)
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
        
        if (authentication == null || !authentication.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Header de autenticación inválido");
        }
        
        final String refreshToken = authentication.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);
        
        if (userEmail == null) return null;
        
        final User user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No se ha encontrado el usuario"));
        
        String deviceId = deviceService.findByUserId(user.getId()).getId();
        
        final boolean isTokenValid = jwtService.isTokenValid(refreshToken, user.getEmail());
        if (!isTokenValid || tokenRepository.findByToken(refreshToken).isEmpty()) return null;
        
        final boolean isStored = tokenRepository.findByToken(refreshToken)
                .map(token -> !token.isExpired() && !token.isRevoked())
                .orElse(false);
        
        if (!isStored) return null;
        
        final String accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user.getId());
        
        return new TokenResponse(accessToken, refreshToken, user.getId(), deviceId, user.getRole().getDescription());
    }
}
