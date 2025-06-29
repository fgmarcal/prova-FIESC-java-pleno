package org.fiesc.felipe.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {
    @Value("${backend-allowed-origin}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(allowedOrigins));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        CorsConfigurationSource source = (CorsConfigurationSource) new UrlBasedCorsConfigurationSource();
        ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
