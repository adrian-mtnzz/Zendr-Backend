package com.zendr.backend.api.controllers;

import com.zendr.backend.internal.token.model.AuthRequest;
import com.zendr.backend.internal.token.model.RegisterRequest;
import com.zendr.backend.internal.token.model.TokenResponse;
import com.zendr.backend.services.auth.AuthService;
import com.zendr.backend.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService service;
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid RegisterRequest request) {
        final TokenResponse response = service.register(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody AuthRequest request) {
        final TokenResponse response = service.authenticate(request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/reset-password")
    public ResponseEntity<Map<String, Boolean>> updatePassword(@RequestBody Map<String, String> body) {
        
        String email = body.get("email");
        String code = body.get("code");
        String password = body.get("password");
        
        Map<String, Boolean> response = new HashMap<>();
        
        response.put(
                "updated",
                userService.updatePassword(code, email, password)
        );
        
        return ResponseEntity.ok(response);
    }
    
    
    @PostMapping("/refresh-token")
    public TokenResponse refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication) {
        return service.refreshToken(authentication);
    }
}