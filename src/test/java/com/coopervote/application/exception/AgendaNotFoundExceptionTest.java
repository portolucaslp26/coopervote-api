package com.coopervote.application.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgendaNotFoundExceptionTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create exception with agenda ID")
        void shouldCreateExceptionWithAgendaId() {
            AgendaNotFoundException exception = new AgendaNotFoundException(42L);

            assertThat(exception.getAgendaId()).isEqualTo(42L);
            assertThat(exception.getMessage()).contains("42");
        }
    }

    @Nested
    @DisplayName("getAgendaId")
    class GetAgendaId {

        @Test
        @DisplayName("should return agenda ID")
        void shouldReturnAgendaId() {
            AgendaNotFoundException exception = new AgendaNotFoundException(1L);

            assertThat(exception.getAgendaId()).isEqualTo(1L);
        }
    }
}