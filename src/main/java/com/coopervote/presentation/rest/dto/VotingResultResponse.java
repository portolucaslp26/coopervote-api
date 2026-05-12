package com.coopervote.presentation.rest.dto;

public record VotingResultResponse(
        Long sessionId,
        Long agendaId,
        long yesVotes,
        long noVotes,
        long totalVotes,
        String result
) {
    public static VotingResultResponse create(Long sessionId, Long agendaId, long yesVotes, long noVotes) {
        long totalVotes = yesVotes + noVotes;
        String result;
        if (yesVotes > noVotes) {
            result = "APPROVED";
        } else if (noVotes > yesVotes) {
            result = "REJECTED";
        } else {
            result = "DRAW";
        }
        return new VotingResultResponse(sessionId, agendaId, yesVotes, noVotes, totalVotes, result);
    }
}