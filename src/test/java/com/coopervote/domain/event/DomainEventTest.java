package com.coopervote.domain.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventTest {

    @Nested
    @DisplayName("VoteCastEvent")
    class VoteCastEventTests {

        @Test
        @DisplayName("should create event with all fields")
        void shouldCreateEventWithAllFields() {
            VoteCastEvent event = new VoteCastEvent(1L, "12345678901", true);

            assertThat(event.getSessionId()).isEqualTo(1L);
            assertThat(event.getAssociateCpf()).isEqualTo("12345678901");
            assertThat(event.getVoteValue()).isTrue();
            assertThat(event.getEventId()).isNotNull();
            assertThat(event.getOccurredOn()).isNotNull();
        }

        @Test
        @DisplayName("should generate unique event IDs")
        void shouldGenerateUniqueEventIds() {
            VoteCastEvent event1 = new VoteCastEvent(1L, "12345678901", true);
            VoteCastEvent event2 = new VoteCastEvent(1L, "12345678901", true);

            assertThat(event1.getEventId()).isNotEqualTo(event2.getEventId());
        }
    }

    @Nested
    @DisplayName("SessionOpenedEvent")
    class SessionOpenedEventTests {

        @Test
        @DisplayName("should create event with all fields")
        void shouldCreateEventWithAllFields() {
            SessionOpenedEvent event = new SessionOpenedEvent(1L, 10L);

            assertThat(event.getSessionId()).isEqualTo(1L);
            assertThat(event.getAgendaId()).isEqualTo(10L);
            assertThat(event.getEventId()).isNotNull();
            assertThat(event.getOccurredOn()).isNotNull();
        }
    }

    @Nested
    @DisplayName("SessionClosedEvent")
    class SessionClosedEventTests {

        @Test
        @DisplayName("should create event with all fields")
        void shouldCreateEventWithAllFields() {
            SessionClosedEvent event = new SessionClosedEvent(1L, 10L, 5L, 3L);

            assertThat(event.getSessionId()).isEqualTo(1L);
            assertThat(event.getAgendaId()).isEqualTo(10L);
            assertThat(event.getYesVotes()).isEqualTo(5L);
            assertThat(event.getNoVotes()).isEqualTo(3L);
            assertThat(event.getEventId()).isNotNull();
            assertThat(event.getOccurredOn()).isNotNull();
        }
    }

    @Nested
    @DisplayName("InMemoryDomainEventPublisher")
    class InMemoryDomainEventPublisherTests {

        @Test
        @DisplayName("should publish VoteCastEvent without errors")
        void shouldPublishVoteCastEventWithoutErrors() {
            InMemoryDomainEventPublisher publisher = new InMemoryDomainEventPublisher();
            VoteCastEvent event = new VoteCastEvent(1L, "12345678901", true);

            publisher.publish(event);
        }

        @Test
        @DisplayName("should publish SessionOpenedEvent without errors")
        void shouldPublishSessionOpenedEventWithoutErrors() {
            InMemoryDomainEventPublisher publisher = new InMemoryDomainEventPublisher();
            SessionOpenedEvent event = new SessionOpenedEvent(1L, 10L);

            publisher.publish(event);
        }

        @Test
        @DisplayName("should publish SessionClosedEvent without errors")
        void shouldPublishSessionClosedEventWithoutErrors() {
            InMemoryDomainEventPublisher publisher = new InMemoryDomainEventPublisher();
            SessionClosedEvent event = new SessionClosedEvent(1L, 10L, 5L, 3L);

            publisher.publish(event);
        }
    }
}
