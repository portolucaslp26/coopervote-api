package com.coopervote.presentation.rest.dto;

public record VotingResultResponse(
        Long sessionId,
        Long agendaId,
        long yesVotes,
        long noVotes,
        long totalVotes
) {
    public static VotingResultResponse create(Long sessionId, Long agendaId, long yesVotes, long noVotes) {
        return new VotingResultResponse(sessionId, agendaId, yesVotes, noVotes, yesVotes + noVotes);
    }
}