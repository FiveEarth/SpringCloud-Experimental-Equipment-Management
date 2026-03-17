package edu.graduation.reserve.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "设备实体")
public class Equipment {
    @Schema(description = "自增ID（插入后回填）")
    private Integer id;
    @Schema(description = "设备编号")
    private String equipment_code;
    @Schema(description = "设备名称")
    private String equipment_name;
    @Schema(description = "设备型号")
    private String model;
    @Schema(description = "实验室ID")
    private Long lab_id;
    @Schema(description = "实验室名称（冗余展示）")
    private String lab_name;
    @Schema(description = "说明书URL")
    private String manual_url;
    @Schema(description = "采购日期")
    private Date purchase_date;
    @Schema(description = "设备规格参数")
    private String specification;
    @Schema(description = "设备状态(0-在库，1-领用中，2-故障待修，3-已报废)")
    private Integer status;
    @Schema(description = "设备状态文本")
    private String status_text;
    @Schema(description = "库存数量")
    private Integer count;
    @Schema(description = "残值")
    private String residual_value;
    @Schema(description = "创造时间")
    private Date create_time;
    @Schema(description = "更新时间")
    private Date update_time;

}
