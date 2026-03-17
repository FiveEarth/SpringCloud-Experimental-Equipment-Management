package edu.graduation.reserve.config;

import feign.Logger;
import feign.Retryer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;



@Configuration
public class ReserveConfig {
    @LoadBalanced //注解式负载均衡
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    Logger.Level feignLoggerLeveL(){
        return Logger.Level.FULL;
    }

    //重试
//    @Bean
    Retryer retryer(){
        return new Retryer.Default();
        }
}
