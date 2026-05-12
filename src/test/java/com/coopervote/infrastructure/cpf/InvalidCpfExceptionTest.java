package com.coopervote.infrastructure.cpf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidCpfExceptionTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create exception with CPF and correct message")
        void shouldCreateExceptionWithCpfAndCorrectMessage() {
            String invalidCpf = "1234567890a";

            InvalidCpfException exception = new InvalidCpfException(invalidCpf);

            assertThat(exception.getMessage()).isEqualTo("CPF invalido: " + invalidCpf);
            assertThat(exception.getCpf()).isEqualTo(invalidCpf);
        }

        @Test
        @DisplayName("should store CPF correctly")
        void shouldStoreCpfCorrectly() {
            String cpf = "98765432109";

            InvalidCpfException exception = new InvalidCpfException(cpf);

            assertThat(exception.getCpf()).isEqualTo(cpf);
        }

        @Test
        @DisplayName("should handle null CPF")
        void shouldHandleNullCpf() {
            InvalidCpfException exception = new InvalidCpfException(null);

            assertThat(exception.getMessage()).isEqualTo("CPF invalido: null");
            assertThat(exception.getCpf()).isNull();
        }

        @Test
        @DisplayName("should handle empty CPF")
        void shouldHandleEmptyCpf() {
            InvalidCpfException exception = new InvalidCpfException("");

            assertThat(exception.getMessage()).isEqualTo("CPF invalido: ");
            assertThat(exception.getCpf()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getCpf")
    class GetCpf {

        @Test
        @DisplayName("should return the CPF passed to constructor")
        void shouldReturnCpfPassedToConstructor() {
            String cpf = "11122233344";

            InvalidCpfException exception = new InvalidCpfException(cpf);

            assertThat(exception.getCpf()).isSameAs(cpf);
        }
    }
}
