package edu.graduation.device.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DeviceServiceConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
