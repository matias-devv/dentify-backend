package com.floss.odontologia.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "API para Clinica Odontologica",
                version = "1.0",
                description = "API REST para gestión de pacientes, dentistas, secretarios"
        )
)
@Configuration
public class OpenApiConfiguration {
}
