package com.coopervote.application.service;

import com.coopervote.application.exception.DuplicateVoteException;
import com.coopervote.application.exception.SessionClosedException;
import com.coopervote.application.exception.SessionNotFoundException;
import com.coopervote.application.exception.VoteNotAllowedException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class VoteService {

    private static final Logger log = LoggerFactory.getLogger(VoteService.class);

    private final VoteRepository voteRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final CpfValidationClient cpfValidationClient;

    public VoteService(VoteRepository voteRepository,
                       VotingSessionRepository votingSessionRepository,
                       CpfValidationClient cpfValidationClient) {
        this.voteRepository = voteRepository;
        this.votingSessionRepository = votingSessionRepository;
        this.cpfValidationClient = cpfValidationClient;
    }

    public VoteResponse castVote(Long sessionId, CastVoteRequest request) {
        log.info("Processing vote from CPF: {} for session: {}", maskCpf(request.cpf()), sessionId);

        VotingSession session = votingSessionRepository.findByIdWithAgenda(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        validateSessionIsOpen(session);

        CpfStatus cpfStatus = validateCpf(request.cpf());

        try {
            Vote vote = new Vote(session, request.cpf(), request.voteValue());
            Vote saved = voteRepository.save(vote);

            log.info("Vote cast successfully. Session: {}, CPF: {}", sessionId, maskCpf(request.cpf()));
            return VoteResponse.fromEntity(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Duplicate vote attempt. CPF: {} for session: {}", maskCpf(request.cpf()), sessionId);
            throw new DuplicateVoteException(request.cpf(), sessionId);
        }
    }

    private void validateSessionIsOpen(VotingSession session) {
        if (!session.getIsActive() || LocalDateTime.now().isAfter(session.getEndTime())) {
            if (session.getIsActive()) {
                session.setActive(false);
                votingSessionRepository.save(session);
                log.info("Session {} auto-closed due to expiration", session.getId());
            }
            log.warn("Voting session is closed: {}", session.getId());
            throw new SessionClosedException(session.getId());
        }
    }

    private CpfStatus validateCpf(String cpf) {
        try {
            CpfStatus status = cpfValidationClient.validate(cpf);
            if (status == CpfStatus.UNABLE_TO_VOTE) {
                log.warn("CPF is unable to vote: {}", maskCpf(cpf));
                throw new VoteNotAllowedException(cpf, "Status do CPF: " + status);
            }
            return status;
        } catch (InvalidCpfException ex) {
            log.warn("Invalid CPF format: {}", maskCpf(cpf));
            throw new VoteNotAllowedException(cpf, "CPF com formato invalido");
        }
    }

    @Transactional(readOnly = true)
    public VotingResultResponse getVotingResult(Long sessionId) {
        log.info("Fetching voting result for session: {}", sessionId);

        VotingSession session = votingSessionRepository.findByIdWithAgenda(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        // Auto-close if expired
        if (session.getIsActive() && LocalDateTime.now().isAfter(session.getEndTime())) {
            session.setActive(false);
            votingSessionRepository.save(session);
            log.info("Session {} auto-closed due to expiration when fetching result", session.getId());
        }

        long yesVotes = voteRepository.countYesVotes(sessionId);
        long noVotes = voteRepository.countNoVotes(sessionId);

        return VotingResultResponse.create(sessionId, session.getAgenda().getId(), yesVotes, noVotes);
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "****";
        }
        return "***." + cpf.substring(cpf.length() - 4);
    }
}