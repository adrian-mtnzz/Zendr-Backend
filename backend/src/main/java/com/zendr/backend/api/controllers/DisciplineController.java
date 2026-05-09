package com.zendr.backend.api.controllers;

import com.zendr.backend.internal.discipline.model.Discipline;
import com.zendr.backend.services.discipline.DisciplineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/disciplines")
@RequiredArgsConstructor
public class DisciplineController {
    
    private final DisciplineService service;
    
    @PostMapping
    public ResponseEntity<Discipline> createDiscipline(@Valid @RequestBody Discipline discipline) {
        
        Discipline saved = service.save(discipline);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(saved);
    }
    
    @GetMapping()
    public ResponseEntity<List<Discipline>> getAllDisciplines() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Discipline> getDisciplineById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
}
