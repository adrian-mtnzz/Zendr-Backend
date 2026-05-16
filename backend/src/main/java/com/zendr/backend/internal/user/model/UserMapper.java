package com.zendr.backend.internal.user.model;

import com.zendr.backend.services.storage.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final BucketService bucketService;
    
    public UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                bucketService.generatePresignedUrl(user.getProfileImg())
        );
    }
}