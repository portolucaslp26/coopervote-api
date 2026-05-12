package com.coopervote.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();

    @Test
    @DisplayName("coopervoteOpenAPI should create OpenAPI bean")
    void coopervoteOpenAPIShouldCreateOpenAPIBean() {
        OpenAPI openAPI = openApiConfig.coopervoteOpenAPI();

        assertThat(openAPI).isNotNull();
        assertThat(openAPI).isInstanceOf(OpenAPI.class);
    }

    @Test
    @DisplayName("coopervoteOpenAPI should set correct title")
    void coopervoteOpenAPIShouldSetCorrectTitle() {
        OpenAPI openAPI = openApiConfig.coopervoteOpenAPI();

        Info info = openAPI.getInfo();
        assertThat(info.getTitle()).isEqualTo("CooperVote API");
    }

    @Test
    @DisplayName("coopervoteOpenAPI should set correct description")
    void coopervoteOpenAPIShouldSetCorrectDescription() {
        OpenAPI openAPI = openApiConfig.coopervoteOpenAPI();

        Info info = openAPI.getInfo();
        assertThat(info.getDescription()).isEqualTo("Sistema de Votacao Cooperativista - API REST");
    }

    @Test
    @DisplayName("coopervoteOpenAPI should set correct version")
    void coopervoteOpenAPIShouldSetCorrectVersion() {
        OpenAPI openAPI = openApiConfig.coopervoteOpenAPI();

        Info info = openAPI.getInfo();
        assertThat(info.getVersion()).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("coopervoteOpenAPI should set correct contact")
    void coopervoteOpenAPIShouldSetCorrectContact() {
        OpenAPI openAPI = openApiConfig.coopervoteOpenAPI();

        Contact contact = openAPI.getInfo().getContact();
        assertThat(contact.getName()).isEqualTo("Lucas Porto");
        assertThat(contact.getEmail()).isEqualTo("lucas@coopervote.com");
    }
}
