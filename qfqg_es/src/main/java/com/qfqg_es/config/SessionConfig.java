package com.qfqg_es.config;

import com.qfqg_es.helper.SessionInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// web配置文件（web.xml）添加一个拦截器
public class SessionConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(SessionConfig.class);
    @Autowired
    SessionInterceptor sessionInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
    /**
     * 网站配置生成器：添加一个拦截器，拦截路径为"/**"即整个项目
     * 同时放行"/user/**"即登录注册相关的页面
     * */
    @Override
    public void addInterceptors(InterceptorRegistry registry ){
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/**")
                .excludePathPatterns("/loginPage.html","/registerPage.html");
    }
}
