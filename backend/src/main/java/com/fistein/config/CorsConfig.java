package com.fistein.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // İzin verilen origin'ler (development ve production için)
        configuration.setAllowedOrigins(Arrays.asList(
                // Development
                "http://localhost:3000",  // React dev
                "http://localhost:5173",  // Vite dev
                "http://localhost:8081",  // React Native Expo
                // Production
                "https://fistein.info",   // Production domain
                "https://www.fistein.info", // Production www subdomain
                "https://app.fistein.info", // Production app subdomain
                "https://api.fistein.info"  // Production API subdomain
        ));
        
        // İzin verilen HTTP metodları
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // İzin verilen header'lar
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));
        
        // Credential'ların gönderilmesine izin ver
        configuration.setAllowCredentials(true);
        
        // Preflight cache süresi (saniye) - Reduced to prevent caching issues
        configuration.setMaxAge(60L);
        
        // Response'da görünmesine izin verilen header'lar
        configuration.setExposedHeaders(List.of("Authorization", "Cache-Control"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}