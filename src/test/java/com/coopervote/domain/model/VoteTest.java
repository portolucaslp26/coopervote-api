package com.coopervote.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VoteTest {

    private VotingSession sampleSession;

    @BeforeEach
    void setUp() {
        Agenda agenda = new Agenda("Pauta de Teste", "Descricao");
        setEntityId(agenda, 1L);
        sampleSession = new VotingSession(agenda, 60);
        setEntityId(sampleSession, 1L);
    }

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create vote with all fields")
        void shouldCreateVoteWithAllFields() {
            LocalDateTime before = LocalDateTime.now();

            Vote vote = new Vote(sampleSession, "12345678901", true);

            LocalDateTime after = LocalDateTime.now();

            assertThat(vote.getVotingSession()).isEqualTo(sampleSession);
            assertThat(vote.getAssociateCpf()).isEqualTo("12345678901");
            assertThat(vote.getVoteValue()).isTrue();
            assertThat(vote.getCreatedAt()).isAfterOrEqualTo(before);
            assertThat(vote.getCreatedAt()).isBeforeOrEqualTo(after);
        }

        @Test
        @DisplayName("should create vote with false value")
        void shouldCreateVoteWithFalseValue() {
            Vote vote = new Vote(sampleSession, "12345678901", false);

            assertThat(vote.getVoteValue()).isFalse();
        }
    }

    @Nested
    @DisplayName("isYes")
    class IsYes {

        @Test
        @DisplayName("should return true when voteValue is true")
        void shouldReturnTrueWhenVoteValueIsTrue() {
            Vote vote = new Vote(sampleSession, "12345678901", true);

            assertThat(vote.isYes()).isTrue();
        }

        @Test
        @DisplayName("should return false when voteValue is false")
        void shouldReturnFalseWhenVoteValueIsFalse() {
            Vote vote = new Vote(sampleSession, "12345678901", false);

            assertThat(vote.isYes()).isFalse();
        }

        @Test
        @DisplayName("should return false when voteValue is null")
        void shouldReturnFalseWhenVoteValueIsNull() {
            Vote vote = new Vote(sampleSession, "12345678901", true);
            setField(vote, "voteValue", null);

            assertThat(vote.isYes()).isFalse();
        }
    }

    @Nested
    @DisplayName("isNo")
    class IsNo {

        @Test
        @DisplayName("should return true when voteValue is false")
        void shouldReturnTrueWhenVoteValueIsFalse() {
            Vote vote = new Vote(sampleSession, "12345678901", false);

            assertThat(vote.isNo()).isTrue();
        }

        @Test
        @DisplayName("should return false when voteValue is true")
        void shouldReturnFalseWhenVoteValueIsTrue() {
            Vote vote = new Vote(sampleSession, "12345678901", true);

            assertThat(vote.isNo()).isFalse();
        }
    }

    @Nested
    @DisplayName("equals and hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("should be equal when id is the same")
        void shouldBeEqualWhenIdIsSame() {
            Vote vote1 = new Vote(sampleSession, "12345678901", true);
            Vote vote2 = new Vote(sampleSession, "98765432109", false);
            setEntityId(vote1, 1L);
            setEntityId(vote2, 1L);

            assertThat(vote1).isEqualTo(vote2);
            assertThat(vote1.hashCode()).isEqualTo(vote2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when id is different")
        void shouldNotBeEqualWhenIdIsDifferent() {
            Vote vote1 = new Vote(sampleSession, "12345678901", true);
            Vote vote2 = new Vote(sampleSession, "12345678901", true);
            setEntityId(vote1, 1L);
            setEntityId(vote2, 2L);

            assertThat(vote1).isNotEqualTo(vote2);
        }

        @Test
        @DisplayName("should not be equal when compared to null")
        void shouldNotBeEqualWhenComparedToNull() {
            Vote vote = new Vote(sampleSession, "12345678901", true);

            assertThat(vote).isNotEqualTo(null);
        }

        @Test
        @DisplayName("should not be equal when compared to different class")
        void shouldNotBeEqualWhenComparedToDifferentClass() {
            Vote vote = new Vote(sampleSession, "12345678901", true);

            assertThat(vote).isNotEqualTo("not a vote");
        }
    }

    @Nested
    @DisplayName("setVotingSession")
    class SetVotingSession {

        @Test
        @DisplayName("should set voting session")
        void shouldSetVotingSession() {
            Vote vote = new Vote(sampleSession, "12345678901", true);
            Agenda newAgenda = new Agenda("Nova Pauta", "Nova Descricao");
            setEntityId(newAgenda, 2L);
            VotingSession newSession = new VotingSession(newAgenda, 60);
            setEntityId(newSession, 2L);

            vote.setVotingSession(newSession);

            assertThat(vote.getVotingSession()).isEqualTo(newSession);
        }
    }

    private void setEntityId(Object entity, Long id) {
        setField(entity, "id", id);
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
