package com.example.springboot.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = "file:///D:/daima/xm-tingcheguanli/uploads/"; // 👈 加 file:// 和结尾 /

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);

        registry.addResourceHandler("/results/**")
                .addResourceLocations(uploadPath);

        System.out.println("✅ Static resources mapped to: " + uploadPath);
    }
}