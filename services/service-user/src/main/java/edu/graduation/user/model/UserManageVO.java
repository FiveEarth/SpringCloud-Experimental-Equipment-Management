package edu.graduation.user.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserManageVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
    private List<Long> roleIds;
}
