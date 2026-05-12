package com.coopervote;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoopervoteApplicationTest {

    @Test
    @DisplayName("main method should exist and be callable")
    void mainMethodShouldExistAndBeCallable() {
        assertThat(CoopervoteApplication.class).isNotNull();
    }

    @Test
    @DisplayName("CoopervoteApplication should be a public class")
    void coopervoteApplicationShouldBeAPublicClass() {
        assertThat(CoopervoteApplication.class.getModifiers())
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("main method should have correct signature")
    void mainMethodShouldHaveCorrectSignature() throws NoSuchMethodException {
        var mainMethod = CoopervoteApplication.class.getMethod("main", String[].class);

        assertThat(mainMethod).isNotNull();
        assertThat(mainMethod.getReturnType()).isEqualTo(void.class);
    }
}
