package com.coopervote.infrastructure.cpf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CpfValidationTimeoutExceptionTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create exception with message")
        void shouldCreateExceptionWithMessage() {
            CpfValidationTimeoutException exception = new CpfValidationTimeoutException("Timeout occurred");

            assertThat(exception.getMessage()).isEqualTo("Timeout occurred");
            assertThat(exception.getCause()).isNull();
        }

        @Test
        @DisplayName("should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            RuntimeException cause = new RuntimeException("Original error");
            CpfValidationTimeoutException exception = new CpfValidationTimeoutException("Timeout occurred", cause);

            assertThat(exception.getMessage()).isEqualTo("Timeout occurred");
            assertThat(exception.getCause()).isSameAs(cause);
        }

        @Test
        @DisplayName("should handle null message")
        void shouldHandleNullMessage() {
            CpfValidationTimeoutException exception = new CpfValidationTimeoutException(null);

            assertThat(exception.getMessage()).isNull();
        }

        @Test
        @DisplayName("should handle null cause")
        void shouldHandleNullCause() {
            CpfValidationTimeoutException exception = new CpfValidationTimeoutException("Timeout", null);

            assertThat(exception.getCause()).isNull();
        }
    }
}
