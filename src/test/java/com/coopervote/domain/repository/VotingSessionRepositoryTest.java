package com.coopervote.domain.repository;

import com.coopervote.domain.model.Agenda;
import com.coopervote.domain.model.VotingSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VotingSessionRepositoryTest {

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    private Agenda sampleAgenda;

    @BeforeEach
    void setUp() {
        votingSessionRepository.deleteAll();
        agendaRepository.deleteAll();

        sampleAgenda = new Agenda("Pauta de Teste", "Descricao");
        sampleAgenda = agendaRepository.save(sampleAgenda);
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

    @Nested
    @DisplayName("existsByAgendaId")
    class ExistsByAgendaId {

        @Test
        @DisplayName("should return true when session exists for agenda")
        void shouldReturnTrueWhenSessionExists() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            votingSessionRepository.save(session);

            boolean exists = votingSessionRepository.existsByAgendaId(sampleAgenda.getId());

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("should return false when no session exists for agenda")
        void shouldReturnFalseWhenNoSessionExists() {
            boolean exists = votingSessionRepository.existsByAgendaId(sampleAgenda.getId());

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByAgendaId")
    class FindByAgendaId {

        @Test
        @DisplayName("should return session when found by agenda id")
        void shouldReturnSessionWhenFound() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            votingSessionRepository.save(session);

            Optional<VotingSession> found = votingSessionRepository.findByAgendaId(sampleAgenda.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getAgenda().getId()).isEqualTo(sampleAgenda.getId());
        }
    }

    @Nested
    @DisplayName("findByIdWithAgenda")
    class FindByIdWithAgenda {

        @Test
        @DisplayName("should return session with agenda fetched")
        void shouldReturnSessionWithAgendaFetched() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            VotingSession saved = votingSessionRepository.save(session);

            Optional<VotingSession> found = votingSessionRepository.findByIdWithAgenda(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getAgenda()).isNotNull();
            assertThat(found.get().getAgenda().getTitle()).isEqualTo("Pauta de Teste");
        }
    }

    @Nested
    @DisplayName("findActiveSessionsExpiredBefore")
    class FindActiveSessionsExpiredBefore {

        @Test
        @DisplayName("should return active sessions that are expired")
        void shouldReturnExpiredActiveSessions() {
            VotingSession expiredSession = new VotingSession(sampleAgenda, 60);
            setField(expiredSession, "endTime", LocalDateTime.now().minusMinutes(10));
            votingSessionRepository.save(expiredSession);

            VotingSession closedSession = new VotingSession(sampleAgenda, 60);
            closedSession.close();
            votingSessionRepository.save(closedSession);

            VotingSession futureSession = new VotingSession(sampleAgenda, 60);
            setField(futureSession, "endTime", LocalDateTime.now().plusMinutes(30));
            votingSessionRepository.save(futureSession);

            var result = votingSessionRepository.findActiveSessionsExpiredBefore(LocalDateTime.now());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(expiredSession.getId());
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("should save and retrieve session")
        void shouldSaveAndRetrieveSession() {
            VotingSession session = new VotingSession(sampleAgenda, 30);
            VotingSession saved = votingSessionRepository.save(session);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStartTime()).isNotNull();
            assertThat(saved.getEndTime()).isNotNull();
        }
    }
}