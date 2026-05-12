package com.coopervote.application.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionNotFoundExceptionTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create exception with session ID")
        void shouldCreateExceptionWithSessionId() {
            SessionNotFoundException exception = new SessionNotFoundException(3L);

            assertThat(exception.getSessionId()).isEqualTo(3L);
            assertThat(exception.getMessage()).contains("3");
        }
    }

    @Nested
    @DisplayName("getSessionId")
    class GetSessionId {

        @Test
        @DisplayName("should return session ID")
        void shouldReturnSessionId() {
            SessionNotFoundException exception = new SessionNotFoundException(1L);

            assertThat(exception.getSessionId()).isEqualTo(1L);
        }
    }
}