package com.coopervote.application.service;

import com.coopervote.domain.model.VotingSession;
import com.coopervote.presentation.rest.dto.OpenSessionRequest;
import com.coopervote.presentation.rest.dto.VotingSessionResponse;

public interface VotingSessionService {

    VotingSessionResponse openSession(Long agendaId, OpenSessionRequest request);

    VotingSessionResponse getSession(Long sessionId);

    VotingSession getSessionEntity(Long sessionId);

    VotingSessionResponse closeSession(Long sessionId);

    VotingSessionResponse getSessionByAgendaId(Long agendaId);
}
