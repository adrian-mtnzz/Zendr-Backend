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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class UploadController {
    
    private final BucketService storageService;
    
    
    @PostMapping("/user-image")
    public ResponseEntity<String> uploadUserImage(@RequestParam("file") MultipartFile file) {
        
        try {
            String url = storageService.uploadFile(file, "users");
            return ResponseEntity.ok(url);
        
        } catch (IOException e) {
            return ResponseEntity.noContent().build();
        }
        
    }
    
    @PostMapping("/event-image")
    public ResponseEntity<String> uploadEventImage(@RequestParam("file") MultipartFile file) {
        
        try {
            String url = storageService.uploadFile(file, "events");
            return ResponseEntity.ok(url);
            
        } catch (IOException e) {
            return ResponseEntity.noContent().build();
        }
    }
}