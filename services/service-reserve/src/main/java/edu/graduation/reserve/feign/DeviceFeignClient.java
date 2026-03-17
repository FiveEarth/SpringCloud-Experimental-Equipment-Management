package edu.graduation.reserve.feign;

import edu.graduation.common.Result;
import edu.graduation.device.bean.Device;
import edu.graduation.reserve.feign.fallback.DeviceFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-device", fallback = DeviceFeignClientFallback.class, configuration = DeviceFeignConfig.class)
public interface DeviceFeignClient {
    @GetMapping("/device/{id}")
    Result<Device> getDeviceById(@PathVariable("id") Long id);

    /** 预约审批通过时扣减可用数量、驳回/取消时加回 */
    @PutMapping("/equipment/{id}/count")
    void updateEquipmentCount(@PathVariable("id") Long id, @RequestParam("delta") int delta);
}
