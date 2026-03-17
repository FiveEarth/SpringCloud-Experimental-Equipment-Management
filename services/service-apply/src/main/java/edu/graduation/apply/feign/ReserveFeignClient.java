package edu.graduation.apply.feign;

import edu.graduation.common.Result;
import edu.graduation.reserve.bean.Reserve;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * 调用预约服务：按id查预约、领用后标记预约为已领用
 */
@FeignClient(name = "service-reserve", configuration = DeviceFeignConfig.class)
public interface ReserveFeignClient {

    @GetMapping("/getReserveById/{id}")
    Result<Reserve> getReserveById(@PathVariable("id") Long id);

    @PutMapping("/markReserveUsed/{id}")
    void markReserveUsed(@PathVariable("id") Long id);
}
