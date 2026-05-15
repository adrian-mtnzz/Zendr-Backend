package com.zendr.backend.api.controllers;

import com.zendr.backend.services.storage.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class UploadController {
    
    private final BucketService storageService;
    
    
    @PostMapping("/user-image")
    public ResponseEntity<Map<String, String>> uploadUserImage(@RequestParam("file") MultipartFile file) {
        
        try {
            
            Map<String, String> response = new HashMap<>();
            String url = storageService.uploadFile(file, "users");
            response.put("url", url);
            
            return ResponseEntity.ok(response);
        
        } catch (IOException e) {
            return ResponseEntity.noContent().build();
        }
        
    }
    
    @PostMapping("/event-image")
    public ResponseEntity<Map<String, String>> uploadEventImage(@RequestParam("file") MultipartFile file) {
        
        try {
            Map<String, String> response = new HashMap<>();
            String url = storageService.uploadFile(file, "events");
            response.put("url", url);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.noContent().build();
        }
    }
}