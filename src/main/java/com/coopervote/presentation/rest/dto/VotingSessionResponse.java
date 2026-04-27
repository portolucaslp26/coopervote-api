package com.coopervote.presentation.rest.dto;

import com.coopervote.domain.model.VotingSession;

public record VotingSessionResponse(
        Long id,
        Long agendaId,
        String agendaTitle,
        String startTime,
        String endTime,
        Boolean isActive
) {
    public static VotingSessionResponse fromEntity(VotingSession session) {
        return new VotingSessionResponse(
                session.getId(),
                session.getAgenda().getId(),
                session.getAgenda().getTitle(),
                session.getStartTime().toString(),
                session.getEndTime().toString(),
                session.getIsActive()
        );
    }
}