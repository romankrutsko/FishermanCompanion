package com.fisherman.companion.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@ComponentScan("com.fisherman.companion")
public class FishermanCompanionConfig {
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000/", "https://fishermancompanion-production.up.railway.app/", "http://localhost:8080/"));
        config.setAllowedMethods(List.of("GET","POST"));
        config.setAllowedHeaders(List.of("content-type"));
        config.setAllowCredentials(true);
        configSource.registerCorsConfiguration("/**", config);
        return configSource;
    }
}
