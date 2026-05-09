package com.zendr.backend.services.discipline;

import com.zendr.backend.internal.discipline.model.Discipline;
import com.zendr.backend.internal.discipline.repository.DisciplineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DisciplineServiceImpl implements DisciplineService {
    
    private final DisciplineRepository repo;
    
    @Override
    public Discipline save(Discipline discipline) {
        if (repo.existsByNameContainingIgnoreCase(discipline.getName()).isPresent()) throw new IllegalArgumentException(
                "El nombre de la disciplina no es válido. Ya existe una disciplina con ese nombre"
        );
        
        String name = discipline.getName();
        if (name != null) {
            name = name.trim().toLowerCase();
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            discipline.setName(name);
        }
        return repo.save(discipline);
    }
    
    @Override
    public List<Discipline> findAll() {
        return repo.findAll();
    }
    
    @Override
    public Optional<Discipline> findById(String id) {
        return repo.findById(id);
    }
}
