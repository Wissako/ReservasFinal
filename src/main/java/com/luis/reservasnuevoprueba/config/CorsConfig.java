package com.luis.reservasnuevoprueba.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing)
 * Permite que el frontend acceda a la API desde diferentes orígenes
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (URLs desde donde se pueden hacer peticiones)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",           // React local
                "http://localhost:4200",           // Angular local
                "http://localhost:5173",           // Vite local
                "http://localhost:8081",           // Otro puerto local
                "http://127.0.0.1:5500",           // Live Server
                "https://tu-frontend.vercel.app",  // Vercel
                "https://tu-frontend.netlify.app", // Netlify
                "*"                                 // Permite todos (solo para desarrollo)
        ));

        // También puedes usar patrones con allowedOriginPatterns
        // configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        // Headers permitidos en las peticiones
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));

        // Headers que el cliente puede leer en la respuesta
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));

        // Permitir envío de credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Tiempo que el navegador puede cachear la respuesta preflight
        configuration.setMaxAge(3600L);

        // Aplicar configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}