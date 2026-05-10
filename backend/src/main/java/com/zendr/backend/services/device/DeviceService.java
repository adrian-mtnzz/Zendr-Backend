package com.zendr.backend.services.device;

import com.zendr.backend.internal.device.model.Device;

import java.util.Optional;

public interface DeviceService {
    Device findById(String id);
    Device findByUserId(String userId);
    Device save(String userId, String platform, String deviceModel, String ipAddress);
}
