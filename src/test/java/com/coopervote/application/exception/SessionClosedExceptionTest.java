package com.coopervote.application.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionClosedExceptionTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create exception with session ID")
        void shouldCreateExceptionWithSessionId() {
            SessionClosedException exception = new SessionClosedException(7L);

            assertThat(exception.getSessionId()).isEqualTo(7L);
            assertThat(exception.getMessage()).contains("7");
        }
    }

    @Nested
    @DisplayName("getSessionId")
    class GetSessionId {

        @Test
        @DisplayName("should return session ID")
        void shouldReturnSessionId() {
            SessionClosedException exception = new SessionClosedException(1L);

            assertThat(exception.getSessionId()).isEqualTo(1L);
        }
    }
}