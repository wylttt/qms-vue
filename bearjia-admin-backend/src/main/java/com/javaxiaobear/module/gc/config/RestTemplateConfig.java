package com.javaxiaobear.module.gc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 连接超时时间：10秒
        factory.setConnectTimeout(10000);
        // 读取超时时间：30秒
        factory.setReadTimeout(30000);
        
        return new RestTemplate(factory);
    }
}
