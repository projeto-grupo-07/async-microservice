package com.example.ms_report_async.infraestructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Report Async API")
                        .version("1.0.0")
                        .description("API para importação e processamento assíncrono de relatórios")
                        .contact(new Contact()
                                .name("Grupo 7 Brink Calçados")
                                .email("guilherme.figueiredo@sptech.school"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

