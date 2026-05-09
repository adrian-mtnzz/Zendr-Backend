package com.zendr.backend.api.controllers;

import com.zendr.backend.internal.user.model.User;
import com.zendr.backend.internal.user.model.UserDTO;
import com.zendr.backend.internal.user.model.UserMapper;
import com.zendr.backend.services.EmailAuthCode.EmailAuthCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/authCode")
@RequiredArgsConstructor
public class EmailAuthCodeController {
    
    private final EmailAuthCodeService service;
    
    @PostMapping
    public ResponseEntity<Map<String, Instant>> sendAuthCode(@RequestBody String email) {
        
        Map<String, Instant> response = new HashMap<>();
        
        Instant expiration = (email == null || email.trim().isEmpty())? null : service.generateCode(email).orElse(null);
        response.put("expiresAt", expiration);
        
        return ResponseEntity.ok(response);
    }
}
