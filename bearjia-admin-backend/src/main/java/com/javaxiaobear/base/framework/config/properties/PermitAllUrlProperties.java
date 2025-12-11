package com.javaxiaobear.base.framework.config.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.javaxiaobear.base.framework.aspectj.lang.annotation.Anonymous;

/**
 * 设置Anonymous注解允许匿名访问的url
 *
 * @author javaxiaobear
 */
@Configuration
public class PermitAllUrlProperties implements InitializingBean, ApplicationContextAware
{
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    private ApplicationContext applicationContext;

    private List<String> urls = new ArrayList<>();

    public String ASTERISK = "*";

    @Override
    public void afterPropertiesSet()
    {
        try {
            // 使用 @Qualifier 或者 bean 名称来获取特定的 RequestMappingHandlerMapping
            // 避免与 Actuator 的 controllerEndpointHandlerMapping 冲突
            RequestMappingHandlerMapping mapping = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

            map.keySet().forEach(info -> {
                HandlerMethod handlerMethod = map.get(info);

                // 获取方法上边的注解 替代path variable 为 *
                Anonymous method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class);
                if (method != null) {
                    addUrlPatterns(info);
                }

                // 获取类上边的注解, 替代path variable 为 *
                Anonymous controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class);
                if (controller != null) {
                    addUrlPatterns(info);
                }
            });
        } catch (Exception e) {
            System.err.println("Error initializing PermitAllUrlProperties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 添加URL模式到允许列表
     */
    private void addUrlPatterns(RequestMappingInfo info) {
        try {
            // 兼容不同版本的Spring Boot
            if (info.getPatternsCondition() != null && info.getPatternsCondition().getPatterns() != null) {
                info.getPatternsCondition().getPatterns()
                        .forEach(url -> urls.add(RegExUtils.replaceAll(url, PATTERN, ASTERISK)));
            } else if (info.getPathPatternsCondition() != null && info.getPathPatternsCondition().getPatterns() != null) {
                // Spring Boot 2.6+ 使用 PathPatternsCondition
                info.getPathPatternsCondition().getPatterns()
                        .forEach(pattern -> urls.add(RegExUtils.replaceAll(pattern.getPatternString(), PATTERN, ASTERISK)));
            }
        } catch (Exception e) {
            System.err.println("Error adding URL patterns: " + e.getMessage());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.applicationContext = context;
    }

    public List<String> getUrls()
    {
        return urls;
    }

    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }
}
