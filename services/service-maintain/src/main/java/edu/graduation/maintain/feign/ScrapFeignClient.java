package edu.graduation.maintain.feign;

import edu.graduation.common.Result;
import edu.graduation.scrap.bean.Scrap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-scrap", configuration = ScrapFeignConfig.class)
public interface ScrapFeignClient {

    @PostMapping("/scrap")
    Result<Long> createScrap(@RequestBody Scrap scrap);
}
