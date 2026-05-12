package com.coopervote.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    private final CorsConfig corsConfig = new CorsConfig();

    @Test
    @DisplayName("corsFilter should create CorsFilter bean")
    void corsFilterShouldCreateCorsFilterBean() {
        var filter = corsConfig.corsFilter();

        assertThat(filter).isNotNull();
    }
}
