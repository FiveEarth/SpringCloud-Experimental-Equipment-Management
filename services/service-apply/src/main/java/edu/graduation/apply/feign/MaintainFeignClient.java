package edu.graduation.apply.feign;

import edu.graduation.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 归还审批选择「故障」时调用维修服务创建维修记录
 */
@FeignClient(name = "service-maintain", configuration = DeviceFeignConfig.class)
public interface MaintainFeignClient {

    @PostMapping("/maintain")
    Result<Long> createMaintain(@RequestBody Map<String, Object> body);
}
