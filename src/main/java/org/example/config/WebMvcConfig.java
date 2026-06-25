package org.example.config;

import org.example.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// SpringMVC配置类，注册拦截器
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                // 通配放行：登录注册 + 公开查询接口
                .excludePathPatterns(
                        "/**/login",
                        "/**/register",
                        "/**/login/**",
                        "/**/register/**",
                        // 公开接口：无需登录即可访问
                        "/**/merchant/validList",
                        "/**/merchant/detail/**",
                        "/**/merchant/search",
                        "/**/merchant/page",
                        "/**/category/list/**",
                        "/**/product/list/**",
                        "/**/product/listByCategory",
                        "/**/product/detail/**",
                        "/**/order/rider/wait",
                        "/**/rider/online/list"
                );
    }
}