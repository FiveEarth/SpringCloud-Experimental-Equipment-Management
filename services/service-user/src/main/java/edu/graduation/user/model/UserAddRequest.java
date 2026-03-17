package edu.graduation.user.model;

import lombok.Data;

import java.util.List;

@Data
public class UserAddRequest {
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer status = 1;
    private List<Long> roleIds;
}
