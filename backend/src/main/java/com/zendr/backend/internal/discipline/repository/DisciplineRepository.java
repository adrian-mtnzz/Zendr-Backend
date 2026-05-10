package com.zendr.backend.internal.discipline.repository;

import com.zendr.backend.internal.discipline.model.Discipline;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisciplineRepository extends MongoRepository<Discipline, String> {
    boolean existsByNameIgnoreCase(String name);
}
