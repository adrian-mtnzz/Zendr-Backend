package com.zendr.backend.services.device;

import com.zendr.backend.internal.device.model.Device;
import com.zendr.backend.internal.device.repository.DeviceRepository;
import com.zendr.backend.internal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    
    private final DeviceRepository repository;
    private final UserRepository userRepository;
    
    @Override
    public Device save(String userId, String platform, String deviceModel, String ipAddress) {
        
        if (userRepository.findById(userId).isEmpty()) throw new IllegalArgumentException("Usuario no encontrado");
        if (ipAddress.length() < 7) throw new IllegalArgumentException("La dirección ip no es válida");
        
        Device device = Device.builder()
                .userId(userId)
                .platform(platform)
                .deviceModel(deviceModel)
                .build();
        
        return repository.save(device);
    }
    
    @Override
    public Device findById(String id) {
        return repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Dispositivo no encontrado")
        );
    }
    
    @Override
    public Device findByUserId(String userId) {
        return repository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("Dispositivo no encontrado")
        );
    }
}
