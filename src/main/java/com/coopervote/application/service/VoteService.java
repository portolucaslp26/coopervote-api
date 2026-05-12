package com.coopervote.application.service;

import com.coopervote.presentation.rest.dto.CastVoteRequest;
import com.coopervote.presentation.rest.dto.VoteResponse;
import com.coopervote.presentation.rest.dto.VotingResultResponse;

public interface VoteService {

    VoteResponse castVote(Long sessionId, CastVoteRequest request);

    VotingResultResponse getVotingResult(Long sessionId);
}
