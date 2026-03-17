package edu.graduation.device.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备实例：每台实物一条记录，用于领用/归还/检修/报废精确到“哪一台”
 */
@Data
@Schema(description = "设备实例（台账）")
public class Asset {
    @Schema(description = "实例ID")
    private Long id;
    @Schema(description = "所属设备类型ID")
    private Long equipmentId;
    @Schema(description = "实例编号（如 EQ-P2024001-01）")
    private String assetCode;
    @Schema(description = "状态（0-在库，1-领用中，2-维修中，3-已报废）")
    private Integer status;
    @Schema(description = "创建时间")
    private String createTime;
    @Schema(description = "更新时间")
    private String updateTime;
}
