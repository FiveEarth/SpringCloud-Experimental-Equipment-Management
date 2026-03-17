package edu.graduation.apply.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

/**
 * 调用设备服务时携带内部标识，便于设备服务放行（不校验角色）
 */
public class DeviceFeignConfig {

    @Bean
    public RequestInterceptor internalHeaderInterceptor() {
        return (RequestTemplate template) -> template.header("X-Internal", "apply");
    }
}
