package edu.graduation.maintain.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class ScrapFeignConfig {
    @Bean
    public RequestInterceptor internalHeaderInterceptor() {
        return (RequestTemplate template) -> template.header("X-Internal", "maintain");
    }
}
