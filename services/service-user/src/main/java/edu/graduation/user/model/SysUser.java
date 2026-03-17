package edu.graduation.user.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * User entity mapped to sys_user.
 */
@Data
public class SysUser {

    private Long id;

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String email;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

