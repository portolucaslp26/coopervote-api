package com.coopervote.application.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateVoteExceptionTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create exception with CPF and session ID")
        void shouldCreateExceptionWithCpfAndSessionId() {
            DuplicateVoteException exception = new DuplicateVoteException("12345678901", 5L);

            assertThat(exception.getCpf()).isEqualTo("12345678901");
            assertThat(exception.getSessionId()).isEqualTo(5L);
            assertThat(exception.getMessage()).contains("12345678901").contains("5");
        }

        @Test
        @DisplayName("should create exception with masked CPF in message")
        void shouldCreateExceptionWithMaskedCpfInMessage() {
            DuplicateVoteException exception = new DuplicateVoteException("98765432109", 10L);

            assertThat(exception.getMessage()).contains("98765432109");
            assertThat(exception.getMessage()).contains("10");
        }
    }

    @Nested
    @DisplayName("getCpf")
    class GetCpf {

        @Test
        @DisplayName("should return CPF")
        void shouldReturnCpf() {
            DuplicateVoteException exception = new DuplicateVoteException("12345678901", 1L);

            assertThat(exception.getCpf()).isEqualTo("12345678901");
        }
    }

    @Nested
    @DisplayName("getSessionId")
    class GetSessionId {

        @Test
        @DisplayName("should return session ID")
        void shouldReturnSessionId() {
            DuplicateVoteException exception = new DuplicateVoteException("12345678901", 99L);

            assertThat(exception.getSessionId()).isEqualTo(99L);
        }
    }
}