package com.coopervote.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI coopervoteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CooperVote API")
                        .description("Sistema de Votacao Cooperativista - API REST")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Lucas Porto")
                                .email("lucas@coopervote.com")));
    }
}