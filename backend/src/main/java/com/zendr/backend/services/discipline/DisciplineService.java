package com.zendr.backend.services.discipline;

import com.zendr.backend.internal.discipline.model.Discipline;

import java.util.List;
import java.util.Optional;

public interface DisciplineService {
    Discipline save(Discipline discipline);
    List<Discipline> findAll();
    Optional<Discipline> findById(String id);
}
