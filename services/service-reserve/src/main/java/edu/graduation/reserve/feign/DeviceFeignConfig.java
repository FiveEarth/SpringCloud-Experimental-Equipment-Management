package edu.graduation.reserve.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

/**
 * 调用设备服务时携带内部标识（预约审批锁定/释放数量）
 */
public class DeviceFeignConfig {

    @Bean
    public RequestInterceptor internalHeaderInterceptor() {
        return (RequestTemplate template) -> template.header("X-Internal", "reserve");
    }
}
