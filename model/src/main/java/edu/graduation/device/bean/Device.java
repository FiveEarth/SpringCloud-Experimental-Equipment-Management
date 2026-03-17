package edu.graduation.device.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Device {
    private Long deviceId;
    private String deviceName;
    /** 设备编号（唯一，如 EQ2024001） */
    private String equipmentCode;
    private String model;
    private Long labId;
    /** 所属实验室名称（冗余，用于前端展示） */
    private String labName;
    /** 采购日期 */
    private Date purchaseDate;
    /** 设备规格参数 */
    private String specification;
    /** 说明书路径（PDF） */
    private String manualUrl;
    private Integer status;
    /** 设备状态文本（在库/领用中/故障待修/已报废） */
    private String statusText;
    /** 可借数量：列表以在库实例数展示；设备类型总数量由 lab_equipment.count 维护 */
    private Integer count;
    /** 设备总数（lab_equipment.count，用于预约数量上限） */
    private Integer totalCount;
    /** 残值（报废时填写） */
    private BigDecimal residualValue;
}
