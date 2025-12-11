package com.javaxiaobear;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author javaxiaobear
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class JavaXiaoBearApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(JavaXiaoBearApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  小熊学Java 脚手架 启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
