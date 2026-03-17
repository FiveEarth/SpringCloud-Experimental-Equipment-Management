package edu.graduation.user.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Login response payload.
 */
@Data
@Builder
public class UserLoginResponse {

    private Long userId;

    private String username;

    private String realName;

    private List<String> roles;

    private List<String> permissions;

    private String token;
}

