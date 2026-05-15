package com.zendr.backend.api.controllers;

import com.zendr.backend.services.geocoding.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/geocode")
public class GeocodingController {
    
    private final GeocodingService service;
    
    @PostMapping
    public ResponseEntity<Page<Map<String, Object>>> getSearchLocations(@RequestBody Map<String, String> body, Pageable page) {
        
        if (body.get("search") != null) {
            return ResponseEntity.ok(service.getLocationsBySearch(body.get("search"), page));
        }
        else return ResponseEntity.notFound().build();
    }
}
