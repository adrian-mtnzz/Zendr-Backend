package com.zendr.backend.internal.discipline.repository;

import com.zendr.backend.internal.discipline.model.Discipline;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplineRepository extends MongoRepository<Discipline, String> {
}
