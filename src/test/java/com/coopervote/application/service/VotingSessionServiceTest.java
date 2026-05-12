package com.coopervote.application.service;

import com.coopervote.application.exception.AgendaNotFoundException;
import com.coopervote.application.exception.SessionAlreadyExistsException;
import com.coopervote.application.exception.SessionNotFoundException;
import com.coopervote.domain.model.Agenda;
import com.coopervote.domain.model.VotingSession;
import com.coopervote.domain.repository.AgendaRepository;
import com.coopervote.domain.repository.VotingSessionRepository;
import com.coopervote.presentation.rest.dto.OpenSessionRequest;
import com.coopervote.presentation.rest.dto.VotingSessionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingSessionServiceTest {

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private VotingSessionService votingSessionService;

    private Agenda sampleAgenda;

    @BeforeEach
    void setUp() {
        sampleAgenda = new Agenda("Pauta de Teste", "Descricao da pauta");
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
    @DisplayName("openSession")
    class OpenSession {

        @Test
        @DisplayName("should open session with default duration")
        void shouldOpenSessionWithDefaultDuration() {
            when(agendaRepository.findById(1L)).thenReturn(Optional.of(sampleAgenda));
            when(votingSessionRepository.existsByAgendaId(1L)).thenReturn(false);
            when(votingSessionRepository.save(any(VotingSession.class))).thenAnswer(invocation -> {
                VotingSession saved = invocation.getArgument(0);
                setEntityId(saved, 1L);
                return saved;
            });

            OpenSessionRequest request = new OpenSessionRequest(null);
            VotingSessionResponse response = votingSessionService.openSession(1L, request);

            assertThat(response).isNotNull();
            assertThat(response.agendaId()).isEqualTo(1L);
            assertThat(response.isActive()).isTrue();

            ArgumentCaptor<VotingSession> sessionCaptor = ArgumentCaptor.forClass(VotingSession.class);
            verify(votingSessionRepository).save(sessionCaptor.capture());
            VotingSession captured = sessionCaptor.getValue();
            assertThat(captured.getEndTime()).isAfter(captured.getStartTime());
        }

        @Test
        @DisplayName("should open session with custom duration")
        void shouldOpenSessionWithCustomDuration() {
            when(agendaRepository.findById(1L)).thenReturn(Optional.of(sampleAgenda));
            when(votingSessionRepository.existsByAgendaId(1L)).thenReturn(false);
            when(votingSessionRepository.save(any(VotingSession.class))).thenAnswer(invocation -> {
                VotingSession saved = invocation.getArgument(0);
                setEntityId(saved, 1L);
                return saved;
            });

            OpenSessionRequest request = new OpenSessionRequest(30);
            VotingSessionResponse response = votingSessionService.openSession(1L, request);

            assertThat(response).isNotNull();

            ArgumentCaptor<VotingSession> sessionCaptor = ArgumentCaptor.forClass(VotingSession.class);
            verify(votingSessionRepository).save(sessionCaptor.capture());
            VotingSession captured = sessionCaptor.getValue();
            long durationMinutes = java.time.Duration.between(captured.getStartTime(), captured.getEndTime()).toMinutes();
            assertThat(durationMinutes).isEqualTo(30);
        }

        @Test
        @DisplayName("should throw AgendaNotFoundException when agenda not found")
        void shouldThrowWhenAgendaNotFound() {
            when(agendaRepository.findById(999L)).thenReturn(Optional.empty());

            OpenSessionRequest request = new OpenSessionRequest(null);

            assertThatThrownBy(() -> votingSessionService.openSession(999L, request))
                    .isInstanceOf(AgendaNotFoundException.class)
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("should throw SessionAlreadyExistsException when session already exists")
        void shouldThrowWhenSessionAlreadyExists() {
            when(agendaRepository.findById(1L)).thenReturn(Optional.of(sampleAgenda));
            when(votingSessionRepository.existsByAgendaId(1L)).thenReturn(true);

            OpenSessionRequest request = new OpenSessionRequest(null);

            assertThatThrownBy(() -> votingSessionService.openSession(1L, request))
                    .isInstanceOf(SessionAlreadyExistsException.class)
                    .hasMessageContaining("1");
        }
    }

    @Nested
    @DisplayName("getSession")
    class GetSession {

        @Test
        @DisplayName("should return session when found")
        void shouldReturnSessionWhenFound() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            setEntityId(session, 1L);

            when(votingSessionRepository.findByIdWithAgenda(1L)).thenReturn(Optional.of(session));

            VotingSessionResponse response = votingSessionService.getSession(1L);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.agendaId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should throw SessionNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(votingSessionRepository.findByIdWithAgenda(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> votingSessionService.getSession(999L))
                    .isInstanceOf(SessionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getSessionEntity")
    class GetSessionEntity {

        @Test
        @DisplayName("should return session entity when found")
        void shouldReturnSessionEntityWhenFound() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            setEntityId(session, 1L);

            when(votingSessionRepository.findByIdWithAgenda(1L)).thenReturn(Optional.of(session));

            VotingSession result = votingSessionService.getSessionEntity(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("closeSession")
    class CloseSession {

        @Test
        @DisplayName("should close session successfully")
        void shouldCloseSessionSuccessfully() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            setEntityId(session, 1L);

            when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(votingSessionRepository.save(any(VotingSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

            VotingSessionResponse response = votingSessionService.closeSession(1L);

            assertThat(response).isNotNull();
            assertThat(response.isActive()).isFalse();
        }

        @Test
        @DisplayName("should throw SessionNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(votingSessionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> votingSessionService.closeSession(999L))
                    .isInstanceOf(SessionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getSessionByAgendaId")
    class GetSessionByAgendaId {

        @Test
        @DisplayName("should return session when found by agenda id")
        void shouldReturnSessionByAgendaId() {
            VotingSession session = new VotingSession(sampleAgenda, 60);
            setEntityId(session, 1L);

            when(votingSessionRepository.findByAgendaId(1L)).thenReturn(Optional.of(session));

            VotingSessionResponse response = votingSessionService.getSessionByAgendaId(1L);

            assertThat(response).isNotNull();
            assertThat(response.agendaId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should throw SessionNotFoundException when not found")
        void shouldThrowWhenNotFoundByAgendaId() {
            when(votingSessionRepository.findByAgendaId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> votingSessionService.getSessionByAgendaId(999L))
                    .isInstanceOf(SessionNotFoundException.class);
        }
    }
}