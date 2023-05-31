package com.fisherman.companion.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
@ComponentScan("com.fisherman.companion")
public class FishermanCompanionConfig {
    @Value("${gcp.project.id}")
    private String projectId;

    @Value("${gcp.credentials}")
    private String credentialsJson;

    @Bean
    public Storage storage() throws IOException {
        final Credentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(credentialsJson.getBytes()));

        final StorageOptions storageOptions = StorageOptions.newBuilder()
                                                            .setCredentials(credentials)
                                                            .setProjectId(projectId)
                                                            .build();

        return storageOptions.getService();
    }
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("https://fishermancompanion-production.up.railway.app");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("https://fisherman-companion-frontend.vercel.app");
        config.addAllowedOrigin("https://fisherman-companion-frontend-flax.vercel.app");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
