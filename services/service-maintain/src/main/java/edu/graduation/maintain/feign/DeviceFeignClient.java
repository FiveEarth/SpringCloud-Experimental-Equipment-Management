package edu.graduation.maintain.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-device", configuration = DeviceFeignConfig.class)
public interface DeviceFeignClient {

    @PutMapping("/equipment/{id}/status")
    void updateEquipmentStatus(@PathVariable("id") Long equipmentId, @RequestParam("status") Integer status);

    @PutMapping("/equipment/{id}/count")
    void updateEquipmentCount(@PathVariable("id") Long id, @RequestParam("delta") int delta);

    @PutMapping("/asset/{assetId}/status")
    void setAssetStatus(@PathVariable("assetId") Long assetId, @RequestParam("status") Integer status);
}
