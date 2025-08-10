package com.easypg.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL path /uploads/** to local folder upload/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:upload/");  // relative to project root, or use absolute path like "file:/home/user/myapp/upload/"
    }
}