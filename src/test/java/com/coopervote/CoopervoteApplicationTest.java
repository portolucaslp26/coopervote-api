package com.coopervote;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CoopervoteApplication.class)
@ActiveProfiles("test")
class CoopervoteApplicationTest {

    @Test
    @DisplayName("CoopervoteApplication should be loadable")
    void coopervoteApplicationShouldBeLoadable() {
        assertThat(CoopervoteApplication.class).isNotNull();
    }

    @Test
    @DisplayName("main method should have correct signature")
    void mainMethodShouldHaveCorrectSignature() throws NoSuchMethodException {
        var mainMethod = CoopervoteApplication.class.getMethod("main", String[].class);

        assertThat(mainMethod).isNotNull();
        assertThat(mainMethod.getReturnType()).isEqualTo(void.class);
    }
}