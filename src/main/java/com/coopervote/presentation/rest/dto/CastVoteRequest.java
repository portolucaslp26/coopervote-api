package com.coopervote.presentation.rest.dto;

public record CastVoteRequest(
        String cpf,
        Boolean voteValue
) {}