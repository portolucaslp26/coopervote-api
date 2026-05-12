package com.coopervote.application.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionAlreadyExistsExceptionTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create exception with agenda ID")
        void shouldCreateExceptionWithAgendaId() {
            SessionAlreadyExistsException exception = new SessionAlreadyExistsException(5L);

            assertThat(exception.getAgendaId()).isEqualTo(5L);
            assertThat(exception.getMessage()).contains("5");
        }
    }

    @Nested
    @DisplayName("getAgendaId")
    class GetAgendaId {

        @Test
        @DisplayName("should return agenda ID")
        void shouldReturnAgendaId() {
            SessionAlreadyExistsException exception = new SessionAlreadyExistsException(1L);

            assertThat(exception.getAgendaId()).isEqualTo(1L);
        }
    }
}