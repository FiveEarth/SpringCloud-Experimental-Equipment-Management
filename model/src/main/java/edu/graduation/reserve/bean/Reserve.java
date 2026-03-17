package edu.graduation.reserve.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

//-- auto-generated definition
//create table lab_equipment_reserve
//        (
//                id           bigint auto_increment comment '自增ID（预约记录唯一标识）'
//                        primary key,
//                equipment_id bigint                             not null comment '关联设备ID',
//                user_id      bigint                             not null comment '预约人ID（学生）',
//                reserve_date date                               not null comment '预约日期',
//                start_time   time                               not null comment '预约开始时段（如“09:00”）',
//                end_time     time                               not null comment '预约结束时段（如“11:00”）',
//                status       tinyint  default 0                 not null comment '预约状态（0-待确认，1-已确认，2-已取消，3-已完成）',
//                create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
//                update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
//                constraint uk_reserve_conflict
//        unique (equipment_id, reserve_date, start_time, end_time) comment '设备-日期-时段唯一（避免预约冲突）',
//constraint fk_reserve_equipment
//foreign key (equipment_id) references lab_equipment (id),
//constraint fk_reserve_user
//foreign key (user_id) references sys_user (id)
//on delete cascade
//)
//comment '设备预约表：设备删除受限（需先取消预约）；预约人删除时，关联记录同步删除' collate = utf8mb4_unicode_ci;
//
//create index idx_equipment_id
//on lab_equipment_reserve (equipment_id)
//comment '设备ID索引（查询设备预约记录）';
//
//create index idx_reserve_date
//on lab_equipment_reserve (reserve_date)
//comment '预约日期索引（按日期筛选预约）';
//
//create index idx_status
//on lab_equipment_reserve (status)
//comment '预约状态索引（筛选待确认预约）';
//
//create index idx_user_id
//on lab_equipment_reserve (user_id)
//comment '预约人ID索引（查询用户预约记录）';
@Data
//添加springDoc的swagger注解
@Schema(description = "预约申请实体")
public class Reserve {
    @Schema(description = "自增ID")
    private Integer id;

    @Schema(description = "设备ID（关联设备ID）")
    private long equipmentId;

    @Schema(description = "设备名称（冗余展示）")
    private String equipmentName;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "预约人姓名（冗余展示）")
    private String userName;

    @Schema(description = "预约设备数量（默认1，不超过设备总数）")
    private Integer reserveQuantity;

    @Schema(description = "残值")
    private BigDecimal residual_value;

    @Schema(description = "预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date reserveDate;

    @Schema(description = "预约开始时段（如“09:00”）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @Schema(description = "预约结束时段（如“11:00”）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @Schema(description = "预约状态（0-待确认，1-已确认，2-已取消，3-已完成, 4-软删除, 5-删除）")
    private Integer status;

    @Schema(description = "预约状态文本")
    private String statusText;

    @Schema(description = "是否已领用（0-未领用，1-已领用）")
    private Integer isUsed;

    @Schema(description = "软删除前状态（status=4 时用于恢复）")
    private Integer originalStatus;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Schema(description = "预约申请描述")
    private String purpose;

}
