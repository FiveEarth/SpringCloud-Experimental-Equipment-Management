package edu.graduation.user.model;

import lombok.Data;

import java.util.List;

@Data
public class UserUpdateRequest {
    private Long id;
    private String realName;
    private String phone;
    private String email;
    private Integer status;
    private List<Long> roleIds;
}
