package edu.graduation.user.model;

import lombok.Data;

/**
 * Login request payload.
 */
@Data
public class UserLoginRequest {

    private String username;

    private String password;
}

