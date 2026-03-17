package edu.graduation.apply.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 领用审批/归还时通知设备服务更新设备状态与库存数量
 */
@FeignClient(name = "service-device", configuration = DeviceFeignConfig.class)
public interface DeviceFeignClient {

    @PutMapping("/equipment/{id}/status")
    void updateEquipmentStatus(@PathVariable("id") Long equipmentId,
                              @RequestParam("status") Integer status);

    @PutMapping("/equipment/{id}/count")
    void updateEquipmentCount(@PathVariable("id") Long id, @RequestParam("delta") int delta);

    /** 分配一台在库实例，返回 assetId（领用用） */
    @PostMapping("/equipment/{equipmentId}/allocateAsset")
    edu.graduation.common.Result<Long> allocateAsset(@PathVariable("equipmentId") Long equipmentId);

    /** 释放实例（归还在库） */
    @PostMapping("/asset/{assetId}/release")
    void releaseAsset(@PathVariable("assetId") Long assetId);

    /** 更新实例状态（2-维修中 3-已报废） */
    @PutMapping("/asset/{assetId}/status")
    void setAssetStatus(@PathVariable("assetId") Long assetId, @RequestParam("status") Integer status);
}
