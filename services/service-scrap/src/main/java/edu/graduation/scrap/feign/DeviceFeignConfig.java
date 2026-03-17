package edu.graduation.scrap.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DeviceFeignConfig {
    @Bean
    public RequestInterceptor internalHeaderInterceptor() {
        return (RequestTemplate template) -> template.header("X-Internal", "scrap");
    }
}
