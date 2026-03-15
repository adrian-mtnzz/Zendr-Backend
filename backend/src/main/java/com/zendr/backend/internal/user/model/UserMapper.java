package com.zendr.backend.internal.user.model;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                user.getProfileImg()
        );
    }
}