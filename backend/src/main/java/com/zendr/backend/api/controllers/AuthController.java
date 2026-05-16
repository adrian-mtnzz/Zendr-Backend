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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService service;
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(
            @RequestBody @Valid RegisterRequest request,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        final TokenResponse response = service.register(request, file);
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
    
    
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateUniqueParams(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        
        Map<String, Boolean> result = new HashMap<>();
        
        if (username != null && !username.isEmpty()) {
            result.put("usernameExists", userService.existsByUsername(username));
        }
        
        if (email != null && !email.isEmpty()) {
            result.put("emailExists", userService.existsByEmail(email));
        }
        
        if (result.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(result);
    }
}