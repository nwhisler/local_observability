package com.nwhisler.local_observability.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI localObservabilityOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Local Observability Service API")
                        .version("1.0.0")
                        .description("""
                                The local-observability application is designed as a backend for microservices, automated test auditing,\n
                                structured log analytics, deterministic query replay, production-grade pagination, and rule-based alerting.\n
                                Incorporated technologies include Spring Boot, Postgres JSONB storage, and Test-containers integration.\n
                                <br><br>
                                Core guarantees:\n
                                \t• Events persisted in Postgres with JSONB attributes
                                \t• Deterministic ordering enforced: ORDER BY ts ASC, id ASC
                                \t• Stable pagination semantics
                                \t• Server-generated timestamps
                                \t• Sliding time window evaluation of alert rules. (Features cooldown logic.)
                                \t• Automated scheduler evaluates enabled alerts.
                                <br><br>
                                Use Cases:\n
                                \t• Log analytics
                                \t• Test auditing
                                \t• Alerts
                                <br><br>
                                Architecture Principles:\n
                                \t• Application api, service, domain, and persistence separation.
                                \t• Data Transfer Object (DTO) boundaries.
                                \t• Integration-testing guaranteeing deterministc behavior.
                                """)
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")
                ));
    }
}