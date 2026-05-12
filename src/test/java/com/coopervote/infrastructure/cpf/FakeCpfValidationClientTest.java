package com.coopervote.infrastructure.cpf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FakeCpfValidationClientTest {

    private final FakeCpfValidationClient cpfValidationClient = new FakeCpfValidationClient(3000);

    @Nested
    @DisplayName("validate")
    class Validate {

        @Test
        @DisplayName("should return ABLE_TO_VOTE for valid 11-digit CPF")
        void shouldReturnAbleToVoteForValidCpf() {
            CpfStatus status = cpfValidationClient.validate("12345678901");

            assertThat(status).isEqualTo(CpfStatus.ABLE_TO_VOTE);
        }

        @Test
        @DisplayName("should return ABLE_TO_VOTE for another valid CPF")
        void shouldReturnAbleToVoteForAnotherValidCpf() {
            CpfStatus status = cpfValidationClient.validate("98765432109");

            assertThat(status).isEqualTo(CpfStatus.ABLE_TO_VOTE);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should throw InvalidCpfException for null or empty CPF")
        void shouldThrowInvalidCpfExceptionForNullOrEmptyCpf(String cpf) {
            assertThatThrownBy(() -> cpfValidationClient.validate(cpf))
                    .isInstanceOf(InvalidCpfException.class)
                    .hasMessageContaining("CPF invalido");
        }

        @Test
        @DisplayName("should throw InvalidCpfException for CPF shorter than 11 digits")
        void shouldThrowInvalidCpfExceptionForShortCpf() {
            assertThatThrownBy(() -> cpfValidationClient.validate("1234567890"))
                    .isInstanceOf(InvalidCpfException.class)
                    .hasMessageContaining("CPF invalido");
        }

        @Test
        @DisplayName("should throw InvalidCpfException for CPF longer than 11 digits")
        void shouldThrowInvalidCpfExceptionForLongCpf() {
            assertThatThrownBy(() -> cpfValidationClient.validate("123456789012"))
                    .isInstanceOf(InvalidCpfException.class)
                    .hasMessageContaining("CPF invalido");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1234567890a", "abcdefghijk", "12345!67890", "123 456 789 01"})
        @DisplayName("should throw InvalidCpfException for CPF with non-numeric characters")
        void shouldThrowInvalidCpfExceptionForNonNumericCpf(String cpf) {
            assertThatThrownBy(() -> cpfValidationClient.validate(cpf))
                    .isInstanceOf(InvalidCpfException.class)
                    .hasMessageContaining("CPF invalido");
        }

        @Test
        @DisplayName("should throw InvalidCpfException with correct CPF in message")
        void shouldThrowInvalidCpfExceptionWithCorrectCpfInMessage() {
            String invalidCpf = "1234567890a";

            assertThatThrownBy(() -> cpfValidationClient.validate(invalidCpf))
                    .isInstanceOf(InvalidCpfException.class)
                    .hasMessageContaining(invalidCpf);
        }

        @Test
        @DisplayName("should throw CpfValidationTimeoutException on TimeoutException")
        void shouldThrowCpfValidationTimeoutExceptionOnTimeout() throws Exception {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                Future<CpfStatus> slowFuture = executor.submit(() -> {
                    Thread.sleep(10000);
                    return CpfStatus.ABLE_TO_VOTE;
                });

                FakeCpfValidationClient shortTimeoutClient = new FakeCpfValidationClient(50) {
                    @Override
                    public CpfStatus validate(String cpf) {
                        try {
                            return slowFuture.get(50, TimeUnit.MILLISECONDS);
                        } catch (TimeoutException e) {
                            throw new CpfValidationTimeoutException("CPF validation timed out", e);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new CpfValidationTimeoutException("CPF validation interrupted", e);
                        } catch (ExecutionException e) {
                            if (e.getCause() instanceof RuntimeException) {
                                throw (RuntimeException) e.getCause();
                            }
                            throw new CpfValidationTimeoutException("CPF validation failed", e);
                        }
                    }
                };

                assertThatThrownBy(() -> shortTimeoutClient.validate("12345678901"))
                        .isInstanceOf(CpfValidationTimeoutException.class)
                        .hasMessageContaining("timed out");
            } finally {
                executor.shutdownNow();
            }
        }

        @Test
        @DisplayName("should throw CpfValidationTimeoutException on InterruptedException")
        void shouldThrowCpfValidationTimeoutExceptionOnInterruptedException() throws Exception {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                Future<CpfStatus> blockingFuture = executor.submit(() -> {
                    Thread.sleep(10000);
                    return CpfStatus.ABLE_TO_VOTE;
                });

                FakeCpfValidationClient interruptClient = new FakeCpfValidationClient(1000) {
                    @Override
                    public CpfStatus validate(String cpf) {
                        try {
                            return blockingFuture.get(1000, TimeUnit.MILLISECONDS);
                        } catch (TimeoutException e) {
                            throw new CpfValidationTimeoutException("CPF validation timed out", e);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new CpfValidationTimeoutException("CPF validation interrupted", e);
                        } catch (ExecutionException e) {
                            if (e.getCause() instanceof RuntimeException) {
                                throw (RuntimeException) e.getCause();
                            }
                            throw new CpfValidationTimeoutException("CPF validation failed", e);
                        }
                    }
                };

                Thread.currentThread().interrupt();
                try {
                    interruptClient.validate("12345678901");
                } catch (CpfValidationTimeoutException e) {
                    assertThat(e.getMessage()).contains("interrupted");
                } finally {
                    Thread.interrupted();
                }
            } finally {
                executor.shutdownNow();
            }
        }

        @Test
        @DisplayName("should throw IllegalStateException on ExecutionException with RuntimeException cause")
        void shouldThrowRuntimeExceptionOnExecutionExceptionWithRuntimeCause() throws Exception {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                Future<CpfStatus> errorFuture = executor.submit(() -> {
                    throw new IllegalStateException("Some error");
                });

                FakeCpfValidationClient errorClient = new FakeCpfValidationClient(1000) {
                    @Override
                    public CpfStatus validate(String cpf) {
                        try {
                            return errorFuture.get(1000, TimeUnit.MILLISECONDS);
                        } catch (TimeoutException e) {
                            throw new CpfValidationTimeoutException("CPF validation timed out", e);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new CpfValidationTimeoutException("CPF validation interrupted", e);
                        } catch (ExecutionException e) {
                            if (e.getCause() instanceof RuntimeException) {
                                throw (RuntimeException) e.getCause();
                            }
                            throw new CpfValidationTimeoutException("CPF validation failed", e);
                        }
                    }
                };

                assertThatThrownBy(() -> errorClient.validate("12345678901"))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Some error");
            } finally {
                executor.shutdownNow();
            }
        }

        @Test
        @DisplayName("should throw CpfValidationTimeoutException on ExecutionException with checked exception cause")
        void shouldThrowCpfValidationTimeoutExceptionOnExecutionExceptionWithCheckedCause() throws Exception {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                Future<CpfStatus> errorFuture = executor.submit(() -> {
                    throw new Exception("Checked error");
                });

                FakeCpfValidationClient errorClient = new FakeCpfValidationClient(1000) {
                    @Override
                    public CpfStatus validate(String cpf) {
                        try {
                            return errorFuture.get(1000, TimeUnit.MILLISECONDS);
                        } catch (TimeoutException e) {
                            throw new CpfValidationTimeoutException("CPF validation timed out", e);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new CpfValidationTimeoutException("CPF validation interrupted", e);
                        } catch (ExecutionException e) {
                            if (e.getCause() instanceof RuntimeException) {
                                throw (RuntimeException) e.getCause();
                            }
                            throw new CpfValidationTimeoutException("CPF validation failed", e);
                        }
                    }
                };

                assertThatThrownBy(() -> errorClient.validate("12345678901"))
                        .isInstanceOf(CpfValidationTimeoutException.class)
                        .hasMessageContaining("failed");
            } finally {
                executor.shutdownNow();
            }
        }
    }
}