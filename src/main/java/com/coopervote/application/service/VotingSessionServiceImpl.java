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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VotingSessionServiceImpl implements VotingSessionService {

    private static final Logger log = LoggerFactory.getLogger(VotingSessionServiceImpl.class);
    private static final int DEFAULT_DURATION_MINUTES = 1;

    private final VotingSessionRepository votingSessionRepository;
    private final AgendaRepository agendaRepository;

    public VotingSessionServiceImpl(VotingSessionRepository votingSessionRepository, AgendaRepository agendaRepository) {
        this.votingSessionRepository = votingSessionRepository;
        this.agendaRepository = agendaRepository;
    }

    @Override
    public VotingSessionResponse openSession(Long agendaId, OpenSessionRequest request) {
        log.info("Opening voting session for agenda: {}", agendaId);

        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new AgendaNotFoundException(agendaId));

        if (votingSessionRepository.existsByAgendaId(agendaId)) {
            throw new SessionAlreadyExistsException(agendaId);
        }

        int duration = (request != null && request.durationMinutes() != null)
                ? request.durationMinutes()
                : DEFAULT_DURATION_MINUTES;

        VotingSession session = new VotingSession(agenda, duration);
        VotingSession saved = votingSessionRepository.save(session);

        log.info("Voting session opened successfully with id: {} for agenda: {}", saved.getId(), agendaId);
        return VotingSessionResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VotingSessionResponse getSession(Long sessionId) {
        log.debug("Fetching voting session with id: {}", sessionId);

        VotingSession session = votingSessionRepository.findByIdWithAgenda(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        return VotingSessionResponse.fromEntity(session);
    }

    @Override
    @Transactional(readOnly = true)
    public VotingSession getSessionEntity(Long sessionId) {
        return votingSessionRepository.findByIdWithAgenda(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
    }

    @Override
    public VotingSessionResponse closeSession(Long sessionId) {
        log.info("Closing voting session: {}", sessionId);

        VotingSession session = votingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        session.close();
        VotingSession saved = votingSessionRepository.save(session);

        log.info("Voting session closed successfully: {}", sessionId);
        return VotingSessionResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VotingSessionResponse getSessionByAgendaId(Long agendaId) {
        log.debug("Fetching voting session for agenda: {}", agendaId);

        VotingSession session = votingSessionRepository.findByAgendaId(agendaId)
                .orElseThrow(() -> new SessionNotFoundException(agendaId));

        return VotingSessionResponse.fromEntity(session);
    }
}
