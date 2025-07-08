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
        
        // Use allowedOriginPatterns instead of allowedOrigins for better compatibility
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",  // React
                "http://localhost:5173",  // Vite
                "http://localhost:8081"   // React Native Expo
        ));
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        
        // Allow all headers including browser-generated security headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Set to false to avoid conflicts with allowedOriginPatterns
        configuration.setAllowCredentials(false);
        
        // Disable preflight cache during development to avoid caching issues
        configuration.setMaxAge(0L);
        
        // Expose necessary headers that frontend might need
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "Origin", 
                "Access-Control-Request-Method", "Access-Control-Request-Headers",
                "Cache-Control", "Pragma", "Expires"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}