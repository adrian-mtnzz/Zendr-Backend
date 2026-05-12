package com.zendr.backend.internal.waitList.repository;

import com.zendr.backend.internal.waitList.model.WaitList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WaitListRepository extends MongoRepository<WaitList, String> {
}
