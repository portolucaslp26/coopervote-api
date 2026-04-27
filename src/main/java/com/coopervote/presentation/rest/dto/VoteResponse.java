package com.coopervote.presentation.rest.dto;

public record VoteResponse(
        Long id,
        Long sessionId,
        String associateCpf,
        Boolean voteValue,
        String createdAt
) {
    public static VoteResponse fromEntity(com.coopervote.domain.model.Vote vote) {
        return new VoteResponse(
                vote.getId(),
                vote.getVotingSession().getId(),
                vote.getAssociateCpf(),
                vote.getVoteValue(),
                vote.getCreatedAt().toString()
        );
    }
}