package com.andinacapital.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator hospitalRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("paciente-service", r -> r
                .path("/api/pacientes", "/api/pacientes/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://PACIENTE-SERVICE"))
            .route("cita-service", r -> r
                .path("/api/citas", "/api/citas/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://CITA-SERVICE"))
            .build();
    }
}