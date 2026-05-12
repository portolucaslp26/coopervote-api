package com.coopervote.domain.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDomainEventPublisherTest {

    private final InMemoryDomainEventPublisher publisher = new InMemoryDomainEventPublisher();

    @Nested
    @DisplayName("publish")
    class Publish {

        @Test
        @DisplayName("should log VoteCastEvent with masked CPF")
        void shouldLogVoteCastEventWithMaskedCpf() {
            VoteCastEvent event = new VoteCastEvent(1L, "12345678901", true);

            publisher.publish(event);
        }

        @Test
        @DisplayName("should log SessionOpenedEvent")
        void shouldLogSessionOpenedEvent() {
            SessionOpenedEvent event = new SessionOpenedEvent(1L, 10L);

            publisher.publish(event);
        }

        @Test
        @DisplayName("should log SessionClosedEvent")
        void shouldLogSessionClosedEvent() {
            SessionClosedEvent event = new SessionClosedEvent(1L, 10L, 5L, 3L);

            publisher.publish(event);
        }

        @Test
        @DisplayName("should handle VoteCastEvent with null CPF")
        void shouldHandleVoteCastEventWithNullCpf() {
            VoteCastEvent event = new VoteCastEvent(1L, null, true);

            publisher.publish(event);
        }

        @Test
        @DisplayName("should handle VoteCastEvent with CPF shorter than 4 digits")
        void shouldHandleVoteCastEventWithShortCpf() {
            VoteCastEvent event = new VoteCastEvent(1L, "123", true);

            publisher.publish(event);
        }

        @Test
        @DisplayName("should handle VoteCastEvent with CPF of exactly 4 digits")
        void shouldHandleVoteCastEventWithFourDigitCpf() {
            VoteCastEvent event = new VoteCastEvent(1L, "1234", true);

            publisher.publish(event);
        }
    }

    @Nested
    @DisplayName("maskCpf")
    class MaskCpf {

        @Test
        @DisplayName("should return **** for null CPF")
        void shouldReturnStarForNullCpf() throws Exception {
            var method = InMemoryDomainEventPublisher.class.getDeclaredMethod("maskCpf", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(publisher, (String) null);

            assertThat(result).isEqualTo("****");
        }

        @Test
        @DisplayName("should return **** for CPF shorter than 4 characters")
        void shouldReturnStarForShortCpf() throws Exception {
            var method = InMemoryDomainEventPublisher.class.getDeclaredMethod("maskCpf", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(publisher, "123");

            assertThat(result).isEqualTo("****");
        }

        @Test
        @DisplayName("should return **** for empty CPF")
        void shouldReturnStarForEmptyCpf() throws Exception {
            var method = InMemoryDomainEventPublisher.class.getDeclaredMethod("maskCpf", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(publisher, "");

            assertThat(result).isEqualTo("****");
        }

        @Test
        @DisplayName("should mask CPF with more than 4 characters")
        void shouldMaskCpfWithMoreThanFourCharacters() throws Exception {
            var method = InMemoryDomainEventPublisher.class.getDeclaredMethod("maskCpf", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(publisher, "12345678901");

            assertThat(result).isEqualTo("***.8901");
        }

        @Test
        @DisplayName("should mask CPF with exactly 4 characters")
        void shouldMaskCpfWithExactlyFourCharacters() throws Exception {
            var method = InMemoryDomainEventPublisher.class.getDeclaredMethod("maskCpf", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(publisher, "1234");

            assertThat(result).isEqualTo("***.1234");
        }
    }
}