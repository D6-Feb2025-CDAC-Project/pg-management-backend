package com.easypg.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	
	@Value("${app.upload.dir:uploads/rooms}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Convert relative path to absolute path
        String uploadPath = new File(uploadDir).getAbsolutePath() + File.separator;
        
        // Map /uploads/rooms/** URLs to the actual file system path
        registry.addResourceHandler("/uploads/rooms/**")
                .addResourceLocations("file:" + uploadPath);
        
        System.out.println("Configured static resource handler:");
        System.out.println("URL Pattern: /uploads/rooms/**");
        System.out.println("File Location: file:" + uploadPath);
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow CORS for all endpoints
                .allowedOrigins("http://localhost:5173") // Frontend URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow cookies or Authorization headers
    }
}

