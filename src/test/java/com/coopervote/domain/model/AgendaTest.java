package com.coopervote.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AgendaTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create agenda with title and description")
        void shouldCreateAgendaWithTitleAndDescription() {
            LocalDateTime before = LocalDateTime.now();

            Agenda agenda = new Agenda("Titulo da Pauta", "Descricao da Pauta");

            LocalDateTime after = LocalDateTime.now();

            assertThat(agenda.getTitle()).isEqualTo("Titulo da Pauta");
            assertThat(agenda.getDescription()).isEqualTo("Descricao da Pauta");
            assertThat(agenda.getCreatedAt()).isAfterOrEqualTo(before);
            assertThat(agenda.getCreatedAt()).isBeforeOrEqualTo(after);
        }

        @Test
        @DisplayName("should initialize votingSessions as empty list")
        void shouldInitializeVotingSessionsAsEmptyList() {
            Agenda agenda = new Agenda("Titulo", "Descricao");

            assertThat(agenda.getVotingSessions()).isNotNull();
            assertThat(agenda.getVotingSessions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("addVotingSession")
    class AddVotingSession {

        @Test
        @DisplayName("should add voting session and set bidirectional relationship")
        void shouldAddVotingSessionAndSetBidirectionalRelationship() {
            Agenda agenda = new Agenda("Pauta", "Descricao");
            setEntityId(agenda, 1L);
            VotingSession session = new VotingSession(agenda, 60);

            agenda.addVotingSession(session);

            assertThat(agenda.getVotingSessions()).containsExactly(session);
            assertThat(session.getAgenda()).isEqualTo(agenda);
        }

        @Test
        @DisplayName("should add multiple voting sessions")
        void shouldAddMultipleVotingSessions() {
            Agenda agenda = new Agenda("Pauta", "Descricao");
            setEntityId(agenda, 1L);
            VotingSession session1 = new VotingSession(agenda, 60);
            VotingSession session2 = new VotingSession(agenda, 30);

            agenda.addVotingSession(session1);
            agenda.addVotingSession(session2);

            assertThat(agenda.getVotingSessions()).hasSize(2);
            assertThat(agenda.getVotingSessions()).contains(session1, session2);
        }
    }

    @Nested
    @DisplayName("equals and hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("should be equal when id is the same")
        void shouldBeEqualWhenIdIsSame() {
            Agenda agenda1 = new Agenda("Titulo 1", "Descricao 1");
            Agenda agenda2 = new Agenda("Titulo 2", "Descricao 2");
            setEntityId(agenda1, 1L);
            setEntityId(agenda2, 1L);

            assertThat(agenda1).isEqualTo(agenda2);
            assertThat(agenda1.hashCode()).isEqualTo(agenda2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when id is different")
        void shouldNotBeEqualWhenIdIsDifferent() {
            Agenda agenda1 = new Agenda("Titulo", "Descricao");
            Agenda agenda2 = new Agenda("Titulo", "Descricao");
            setEntityId(agenda1, 1L);
            setEntityId(agenda2, 2L);

            assertThat(agenda1).isNotEqualTo(agenda2);
        }

        @Test
        @DisplayName("should not be equal when compared to null")
        void shouldNotBeEqualWhenComparedToNull() {
            Agenda agenda = new Agenda("Titulo", "Descricao");

            assertThat(agenda).isNotEqualTo(null);
        }

        @Test
        @DisplayName("should not be equal when compared to different class")
        void shouldNotBeEqualWhenComparedToDifferentClass() {
            Agenda agenda = new Agenda("Titulo", "Descricao");

            assertThat(agenda).isNotEqualTo("not an agenda");
        }
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
}
