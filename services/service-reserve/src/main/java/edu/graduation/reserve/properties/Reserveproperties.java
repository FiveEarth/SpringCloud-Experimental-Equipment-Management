package edu.graduation.reserve.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "reserve")//配置批量绑定在nacos下,可以无需@RefreshScope就能实现自动刷新
@Data
public class Reserveproperties {

    String Timeout;

    String AutoConfirm;

    String dbUrl;
}
