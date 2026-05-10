package com.zendr.backend.internal.device.repository;

import com.zendr.backend.internal.device.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeviceRepository extends MongoRepository<Device, String> {
    Optional<Device> findByUserId(String userId);
}
