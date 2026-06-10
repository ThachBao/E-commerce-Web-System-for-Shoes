package com.CongNgheJave.ecommerce_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
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
        
        log.info("========== [RESOURCE EXPOSE] ==========");
        log.info("Folder: {}", dirName);
        log.info("Absolute Path: {}", uploadPath);
        log.info("Resource Location: {}", resourceLocation);
        log.info("=======================================");
        
        if (dirName.startsWith("../")) {
            dirName = dirName.replace("../", "");
        }
        
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations(resourceLocation);
    }
}
