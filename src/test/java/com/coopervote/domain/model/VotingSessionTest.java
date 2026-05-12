package com.coopervote.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VotingSessionTest {

    private Agenda sampleAgenda;

    @BeforeEach
    void setUp() {
        sampleAgenda = new Agenda("Pauta de Teste", "Descricao");
        setEntityId(sampleAgenda, 1L);
    }

    private void setEntityId(Object entity, Long id) {
        try {
            java.lang.reflect.Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("isExpired")
    class IsExpired {

        @Test
        @DisplayName("should return false when session is still within time")
        void shouldReturnFalseWhenStillWithinTime() {
            VotingSession session = new VotingSession(sampleAgenda, 60);

            assertThat(session.isExpired()).isFalse();
        }

        @Test
        @DisplayName("should return true when session end time has passed")
        void shouldReturnTrueWhenEndTimeHasPassed() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            setField(session, "endTime", LocalDateTime.now().minusMinutes(1));

            assertThat(session.isExpired()).isTrue();
        }
    }

    @Nested
    @DisplayName("isOpen")
    class IsOpen {

        @Test
        @DisplayName("should return true when session is active and not expired")
        void shouldReturnTrueWhenActiveAndNotExpired() {
            VotingSession session = new VotingSession(sampleAgenda, 60);

            assertThat(session.isOpen()).isTrue();
        }

        @Test
        @DisplayName("should return false when session is manually closed")
        void shouldReturnFalseWhenManuallyClosed() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            session.close();

            assertThat(session.isOpen()).isFalse();
        }

        @Test
        @DisplayName("should return false when session is expired by time")
        void shouldReturnFalseWhenExpiredByTime() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            setField(session, "endTime", LocalDateTime.now().minusMinutes(1));

            assertThat(session.isOpen()).isFalse();
        }

        @Test
        @DisplayName("should return false when session is active but expired")
        void shouldReturnFalseWhenActiveButExpired() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            setField(session, "isActive", true);
            setField(session, "endTime", LocalDateTime.now().minusMinutes(1));

            assertThat(session.isOpen()).isFalse();
        }
    }

    @Nested
    @DisplayName("close")
    class Close {

        @Test
        @DisplayName("should set isActive to false")
        void shouldSetIsActiveToFalse() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            assertThat(session.getIsActive()).isTrue();

            session.close();

            assertThat(session.getIsActive()).isFalse();
        }

        @Test
        @DisplayName("should set endTime to now which is earlier than original planned endTime")
        void shouldSetEndTimeToNowWhichIsEarlierThanOriginal() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            LocalDateTime originalEndTime = session.getEndTime();

            session.close();

            assertThat(session.getEndTime()).isBefore(originalEndTime);
        }
    }

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should set startTime to now")
        void shouldSetStartTimeToNow() {
            LocalDateTime before = LocalDateTime.now();

            VotingSession session = new VotingSession(sampleAgenda, 60);

            LocalDateTime after = LocalDateTime.now();

            assertThat(session.getStartTime()).isAfterOrEqualTo(before);
            assertThat(session.getStartTime()).isBeforeOrEqualTo(after);
        }

        @Test
        @DisplayName("should set endTime based on duration")
        void shouldSetEndTimeBasedOnDuration() {
            VotingSession session = new VotingSession(sampleAgenda, 30);

            long durationMinutes = java.time.Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();

            assertThat(durationMinutes).isEqualTo(30);
        }

        @Test
        @DisplayName("should set isActive to true by default")
        void shouldSetIsActiveToTrueByDefault() {
            VotingSession session = new VotingSession(sampleAgenda, 60);

            assertThat(session.getIsActive()).isTrue();
        }

        @Test
        @DisplayName("should associate agenda")
        void shouldAssociateAgenda() {
            VotingSession session = new VotingSession(sampleAgenda, 60);

            assertThat(session.getAgenda()).isEqualTo(sampleAgenda);
        }
    }

    private void setField(Object entity, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}