package com.fastcampus.project.sns.controller.response;

import com.fastcampus.project.sns.model.User;
import com.fastcampus.project.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String userName;
    private UserRole role;

    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getUserRole()
        );
    }
}
