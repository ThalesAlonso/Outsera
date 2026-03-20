package com.outsera.razzies.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI razziesOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Golden Raspberry Awards API")
                        .description("API para consulta de intervalos minimo e maximo entre vitorias consecutivas por produtor.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Outsera")
                                .url("https://github.com/thalesalonsoo/Outsera"))
                        .license(new License()
                                .name("Uso para avaliacao tecnica")));
    }
}
