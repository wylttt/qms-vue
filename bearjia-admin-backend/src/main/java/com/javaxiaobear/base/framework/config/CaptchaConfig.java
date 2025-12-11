package com.javaxiaobear.base.framework.config;

import org.springframework.context.annotation.Configuration;

/**
 * 验证码配置
 *
 * @author javaxiaobear
 */
@Configuration
public class CaptchaConfig
{
    // EasyCaptcha 不需要额外的 Bean 配置
    // 所有配置都在 CaptchaController 中直接使用
}
