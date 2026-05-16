package com.zendr.backend.internal.user.model;

import com.zendr.backend.services.storage.BucketService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserMapper {

    private static BucketService bucketService;
    
    public static UserDTO toDTO(User user) {
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