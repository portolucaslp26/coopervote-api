package com.coopervote.application.service;

import com.coopervote.application.exception.DuplicateVoteException;
import com.coopervote.application.exception.SessionClosedException;
import com.coopervote.application.exception.SessionNotFoundException;
import com.coopervote.application.exception.VoteNotAllowedException;
import com.coopervote.domain.model.Agenda;
import com.coopervote.domain.model.Vote;
import com.coopervote.domain.model.VotingSession;
import com.coopervote.domain.repository.VoteRepository;
import com.coopervote.domain.repository.VotingSessionRepository;
import com.coopervote.infrastructure.cpf.CpfStatus;
import com.coopervote.infrastructure.cpf.CpfValidationClient;
import com.coopervote.infrastructure.cpf.InvalidCpfException;
import com.coopervote.presentation.rest.dto.CastVoteRequest;
import com.coopervote.presentation.rest.dto.VoteResponse;
import com.coopervote.presentation.rest.dto.VotingResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import com.coopervote.application.service.VoteServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private CpfValidationClient cpfValidationClient;

    @InjectMocks
    private VoteServiceImpl voteService;

    private Agenda sampleAgenda;
    private VotingSession activeSession;
    private VotingSession closedSession;
    private VotingSession expiredSession;

    @BeforeEach
    void setUp() {
        sampleAgenda = new Agenda("Pauta de Teste", "Descricao");
        setEntityId(sampleAgenda, 1L);

        activeSession = new VotingSession(sampleAgenda, 60);
        setEntityId(activeSession, 1L);

        closedSession = new VotingSession(sampleAgenda, 60);
        setEntityId(closedSession, 2L);
        closedSession.close();

        expiredSession = new VotingSession(sampleAgenda, 60);
        setEntityId(expiredSession, 3L);
        setField(expiredSession, "endTime", LocalDateTime.now().minusMinutes(1));
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

    @Nested
    @DisplayName("castVote")
    class CastVote {

        @Test
        @DisplayName("should cast vote successfully")
        void shouldCastVoteSuccessfully() {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(votingSessionRepository.findByIdWithAgenda(1L)).thenReturn(Optional.of(activeSession));
            when(cpfValidationClient.validate("12345678900")).thenReturn(CpfStatus.ABLE_TO_VOTE);
            when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> {
                Vote vote = invocation.getArgument(0);
                setEntityId(vote, 1L);
                return vote;
            });

            VoteResponse response = voteService.castVote(1L, request);

            assertThat(response).isNotNull();
            assertThat(response.voteValue()).isTrue();
            verify(voteRepository).save(any(Vote.class));
        }

        @Test
        @DisplayName("should cast NO vote successfully")
        void shouldCastNoVoteSuccessfully() {
            CastVoteRequest request = new CastVoteRequest("12345678900", false);
            when(votingSessionRepository.findByIdWithAgenda(1L)).thenReturn(Optional.of(activeSession));
            when(cpfValidationClient.validate("12345678900")).thenReturn(CpfStatus.ABLE_TO_VOTE);
            when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> {
                Vote vote = invocation.getArgument(0);
                setEntityId(vote, 1L);
                return vote;
            });

            VoteResponse response = voteService.castVote(1L, request);

            assertThat(response.voteValue()).isFalse();
        }

        @Test
        @DisplayName("should throw SessionNotFoundException when session not found")
        void shouldThrowWhenSessionNotFound() {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(votingSessionRepository.findByIdWithAgenda(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> voteService.castVote(999L, request))
                    .isInstanceOf(SessionNotFoundException.class);
        }

        @Test
        @DisplayName("should throw SessionClosedException when session is manually closed")
        void shouldThrowWhenSessionManuallyClosed() {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(votingSessionRepository.findByIdWithAgenda(2L)).thenReturn(Optional.of(closedSession));

            assertThatThrownBy(() -> voteService.castVote(2L, request))
                    .isInstanceOf(SessionClosedException.class)
                    .hasMessageContaining("2");
        }

        @Test
        @DisplayName("should throw SessionClosedException when session is expired by time")
        void shouldThrowWhenSessionTimeExpired() {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(votingSessionRepository.findByIdWithAgenda(3L)).thenReturn(Optional.of(expiredSession));

            assertThatThrownBy(() -> voteService.castVote(3L, request))
                    .isInstanceOf(SessionClosedException.class);
        }

        @Test
        @DisplayName("should throw VoteNotAllowedException when CPF unable to vote")
        void shouldThrowWhenCpfUnableToVote() {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(votingSessionRepository.findByIdWithAgenda(1L)).thenReturn(Optional.of(activeSession));
            when(cpfValidationClient.validate("12345678900")).thenReturn(CpfStatus.UNABLE_TO_VOTE);

            assertThatThrownBy(() -> voteService.castVote(1L, request))
                    .isInstanceOf(VoteNotAllowedException.class)
                    .hasMessageContaining("UNABLE_TO_VOTE");
        }

        @Test
        @DisplayName("should throw VoteNotAllowedException when CPF is invalid")
        void shouldThrowWhenCpfIsInvalid() {
            CastVoteRequest request = new CastVoteRequest("invalid", true);
            when(votingSessionRepository.findByIdWithAgenda(1L)).thenReturn(Optional.of(activeSession));
            when(cpfValidationClient.validate("invalid")).thenThrow(new InvalidCpfException("invalid"));

            assertThatThrownBy(() -> voteService.castVote(1L, request))
                    .isInstanceOf(VoteNotAllowedException.class)
                    .hasMessageContaining("formato invalido");
        }

        @Test
        @DisplayName("should throw DuplicateVoteException on duplicate vote")
        void shouldThrowOnDuplicateVote() {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(votingSessionRepository.findByIdWithAgenda(1L)).thenReturn(Optional.of(activeSession));
            when(cpfValidationClient.validate("12345678900")).thenReturn(CpfStatus.ABLE_TO_VOTE);
            when(voteRepository.save(any(Vote.class))).thenThrow(DataIntegrityViolationException.class);

            assertThatThrownBy(() -> voteService.castVote(1L, request))
                    .isInstanceOf(DuplicateVoteException.class)
                    .hasMessageContaining("12345678900");
        }
    }

    @Nested
    @DisplayName("getVotingResult")
    class GetVotingResult {

        @Test
        @DisplayName("should return voting result with yes and no votes")
        void shouldReturnVotingResult() {
            when(votingSessionRepository.findByIdWithAgenda(1L)).thenReturn(Optional.of(activeSession));
            when(voteRepository.countYesVotes(1L)).thenReturn(5L);
            when(voteRepository.countNoVotes(1L)).thenReturn(3L);

            VotingResultResponse response = voteService.getVotingResult(1L);

            assertThat(response.sessionId()).isEqualTo(1L);
            assertThat(response.agendaId()).isEqualTo(1L);
            assertThat(response.yesVotes()).isEqualTo(5);
            assertThat(response.noVotes()).isEqualTo(3);
        }

        @Test
        @DisplayName("should return zero votes when no votes cast")
        void shouldReturnZeroVotesWhenNoVotesCast() {
            when(votingSessionRepository.findByIdWithAgenda(1L)).thenReturn(Optional.of(activeSession));
            when(voteRepository.countYesVotes(1L)).thenReturn(0L);
            when(voteRepository.countNoVotes(1L)).thenReturn(0L);

            VotingResultResponse response = voteService.getVotingResult(1L);

            assertThat(response.yesVotes()).isZero();
            assertThat(response.noVotes()).isZero();
        }

        @Test
        @DisplayName("should throw SessionNotFoundException when session not found")
        void shouldThrowWhenSessionNotFoundForResult() {
            when(votingSessionRepository.findByIdWithAgenda(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> voteService.getVotingResult(999L))
                    .isInstanceOf(SessionNotFoundException.class);
        }
    }
}