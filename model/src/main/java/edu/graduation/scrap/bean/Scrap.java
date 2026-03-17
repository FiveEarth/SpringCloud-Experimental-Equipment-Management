package edu.graduation.scrap.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Scrap {
    private Long id;
    private Long equipmentId;
    /** 设备实例ID（报废哪一台，可选） */
    private Long assetId;
    private String equipmentName;
    private String scrapReason;
    private BigDecimal residualValue;
    private Long applyUserId;
    private String applyUserName;
    private Long approvalUserId;
    private String approvalUserName;
    /** 0-待审批 1-通过 2-驳回 */
    private Integer approvalStatus;
    private String disposalMethod;
    private LocalDateTime disposalTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
