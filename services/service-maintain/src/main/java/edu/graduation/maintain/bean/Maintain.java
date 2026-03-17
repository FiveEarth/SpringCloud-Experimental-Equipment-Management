package edu.graduation.maintain.bean;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Maintain {
    private Long id;
    private Long equipmentId;
    /** 设备实例ID（维修哪一台，可选） */
    private Long assetId;
    private String equipmentName;
    private Integer maintainType;
    private Long applyUserId;
    private String applyUserName;
    private Long assignUserId;
    private String assignUserName;
    private String maintainContent;
    private Timestamp maintainTime;
    private BigDecimal cost;
    private Integer progressStatus;
    /** 软删除(progress_status=4)前的状态，用于恢复 */
    private Integer originalStatus;
    private Integer remindCycle;
    private Timestamp nextRemindTime;
    private Timestamp createTime;
    private Timestamp updateTime;
}