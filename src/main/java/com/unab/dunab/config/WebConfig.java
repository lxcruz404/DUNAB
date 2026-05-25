package com.unab.dunab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Configuración de recursos estáticos.
 * Sirve las fotos de perfil subidas desde /uploads/.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos de la carpeta de uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir);
        // Servir archivos estáticos del frontend
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // SPA fallback: rutas sin extensión sirven index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
