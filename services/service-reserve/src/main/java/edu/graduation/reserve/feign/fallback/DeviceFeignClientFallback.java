package edu.graduation.reserve.feign.fallback;

import edu.graduation.common.Result;
import edu.graduation.device.bean.Device;
import edu.graduation.reserve.feign.DeviceFeignClient;
import org.springframework.stereotype.Component;

@Component
public class DeviceFeignClientFallback implements DeviceFeignClient {
    @Override
    public Result<Device> getDeviceById(Long id) {
        return Result.fail("设备服务不可用");
    }

    @Override
    public void updateEquipmentCount(Long id, int delta) {
        // 设备服务不可用时无法锁定/释放数量，调用方会收到异常
    }
}
