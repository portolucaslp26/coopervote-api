package com.coopervote.application.service;

import com.coopervote.domain.model.Agenda;
import com.coopervote.domain.model.VotingSession;
import com.coopervote.domain.repository.VotingSessionRepository;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionExpirationSchedulerTest {

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @InjectMocks
    private SessionExpirationScheduler scheduler;

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
    @DisplayName("closeExpiredSessions")
    class CloseExpiredSessions {

        @Test
        @DisplayName("should close all expired sessions")
        void shouldCloseAllExpiredSessions() {
            VotingSession session1 = new VotingSession(sampleAgenda, 60);
            setEntityId(session1, 1L);
            setField(session1, "endTime", LocalDateTime.now().minusMinutes(10));

            VotingSession session2 = new VotingSession(sampleAgenda, 60);
            setEntityId(session2, 2L);
            setField(session2, "endTime", LocalDateTime.now().minusMinutes(5));

            List<VotingSession> expiredSessions = Arrays.asList(session1, session2);
            when(votingSessionRepository.findActiveSessionsExpiredBefore(any(LocalDateTime.class)))
                    .thenReturn(expiredSessions);
            when(votingSessionRepository.save(any(VotingSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            scheduler.closeExpiredSessions();

            verify(votingSessionRepository, times(2)).save(any(VotingSession.class));
        }

        @Test
        @DisplayName("should not save when no expired sessions")
        void shouldNotSaveWhenNoExpiredSessions() {
            when(votingSessionRepository.findActiveSessionsExpiredBefore(any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            scheduler.closeExpiredSessions();

            verify(votingSessionRepository, never()).save(any(VotingSession.class));
        }

        @Test
        @DisplayName("should close each expired session")
        void shouldCloseEachExpiredSession() {
            VotingSession session1 = new VotingSession(sampleAgenda, 60);
            setEntityId(session1, 1L);
            setField(session1, "endTime", LocalDateTime.now().minusMinutes(10));

            VotingSession session2 = new VotingSession(sampleAgenda, 60);
            setEntityId(session2, 2L);
            setField(session2, "endTime", LocalDateTime.now().minusMinutes(5));

            List<VotingSession> expiredSessions = Arrays.asList(session1, session2);
            when(votingSessionRepository.findActiveSessionsExpiredBefore(any(LocalDateTime.class)))
                    .thenReturn(expiredSessions);
            when(votingSessionRepository.save(any(VotingSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            scheduler.closeExpiredSessions();

            ArgumentCaptor<VotingSession> captor = ArgumentCaptor.forClass(VotingSession.class);
            verify(votingSessionRepository, times(2)).save(captor.capture());

            List<VotingSession> savedSessions = captor.getAllValues();
            assertThat(savedSessions).hasSize(2);
            assertThat(savedSessions.get(0).getIsActive()).isFalse();
            assertThat(savedSessions.get(1).getIsActive()).isFalse();
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