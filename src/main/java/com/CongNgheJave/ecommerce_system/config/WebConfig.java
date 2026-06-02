package com.CongNgheJave.ecommerce_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để Spring Boot có thể phục vụ (serve) các file tĩnh từ thư mục uploads/ bên ngoài (cùng cấp với thư mục src/)
        exposeDirectory("uploads", registry);
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath().replace("\\", "/");
        String resourceLocation = uploadDir.toUri().toString();
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }
        
        System.out.println("========== [RESOURCE EXPOSE] ==========");
        System.out.println("Folder: " + dirName);
        System.out.println("Absolute Path: " + uploadPath);
        System.out.println("Resource Location: " + resourceLocation);
        System.out.println("=======================================");
        
        if (dirName.startsWith("../")) {
            dirName = dirName.replace("../", "");
        }
        
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations(resourceLocation);
    }
}
