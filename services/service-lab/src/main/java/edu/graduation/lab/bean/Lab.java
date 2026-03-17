package edu.graduation.lab.bean;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Laboratory entity mapped to lab_laboratory.
 */
@Data
public class Lab {

    private Long id;

    private String labName;

    private String labLocation;

    private String labManager;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

