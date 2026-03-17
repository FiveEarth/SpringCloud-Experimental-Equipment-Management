package edu.graduation.reserve.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "领用归还")
public class Apply {
    @Schema(description = "自增ID")
    private Integer id;
    @Schema(description = "关联预约ID（无预约则为空）")
    private Long reserve_id;
    @Schema(description = "关联设备ID（lab_equipment.id）")
    private Long equipment_id;
    @Schema(description = "关联设备实例ID（lab_equipment_asset.id，精确到哪一台）")
    private Long asset_id;
    @Schema(description = "实例编号（冗余展示，如 EQ-P2024001-01）")
    private String asset_code;
    @Schema(description = "设备名称（冗余展示）")
    private String equipment_name;
    @Schema(description = "申请人ID")
    private Integer user_id;
    @Schema(description = "申请人姓名（冗余展示）")
    private String user_name;
    @Schema(description = "申请类型（0-领用，1-归还）")
    private Integer apply_type;
    @Schema(description = "申请数量（领用/归还台数，默认1）")
    private Integer apply_quantity;
    @Schema(description = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date apply_time;
    @Schema(description = "领用开始时间(领用申请必填)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date use_time;
    @Schema(description = "归还时间(归还后填写)")
    private String return_time;
    @Schema(description = "领用用途")
    private String purpose;
    @Schema(description = "归还时设备状态")
    private String equipment_status;
    @Schema(description = "审批人ID(管理员)")
    private Integer approve_user_id;
    @Schema(description = "审批人姓名（冗余展示）")
    private String approval_user_name;
    @Schema(description = "审批状态(0-待审批 1-通过 2-驳回)")
    private Integer approve_status;
    @Schema(description = "审批状态文本")
    private String status_text;
    @Schema(description = "审批时间")
    private String approve_time;
    @Schema(description = "备注(如故障描述)")
    private String remarks;
    @Schema(description = "归还状态（0-未申请归还 1-待归还审批 2-已归还）")
    private Integer return_status;
    @Schema(description = "学生申请归还时间")
    private String return_apply_time;
    @Schema(description = "记录状态（4-软删除/已隐藏）")
    private Integer status;
    @Schema(description = "软删除前状态（用于恢复）")
    private Integer original_status;
}
