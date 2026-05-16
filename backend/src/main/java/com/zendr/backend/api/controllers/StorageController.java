package com.zendr.backend.api.controllers;

import com.zendr.backend.services.storage.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/storage")
public class StorageController {
    
    private final BucketService storageService;
    
    
    @PostMapping("/upload/user-image")
    public ResponseEntity<Map<String, String>> uploadUserImage(@RequestParam("file") MultipartFile file) {
        
        try {
            
            Map<String, String> response = new HashMap<>();
            String url = storageService.generatePresignedUrl(storageService.uploadFile(file, "users"));
            response.put("url", url);
            
            return ResponseEntity.ok(response);
        
        } catch (IOException e) {
            return ResponseEntity.noContent().build();
        }
        
    }
    
    @PostMapping("/upload/event-image")
    public ResponseEntity<Map<String, String>> uploadEventImage(@RequestParam("file") MultipartFile file) {
        
        try {
            Map<String, String> response = new HashMap<>();
            String url =  storageService.generatePresignedUrl(storageService.uploadFile(file, "events"));
            response.put("url", url);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.noContent().build();
        }
    }
    
    @GetMapping("/get-resource")
    public ResponseEntity<Map<String, String>> getSignedUrl(
            @RequestParam String key
    ) {
        
        String url = storageService.generatePresignedUrl(key);
        return ResponseEntity.ok(Map.of(
                "url", url
        ));
    }
}