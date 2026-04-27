package com.coopervote.presentation.rest;

import com.coopervote.application.service.VoteService;
import com.coopervote.presentation.rest.dto.CastVoteRequest;
import com.coopervote.presentation.rest.dto.VoteResponse;
import com.coopervote.presentation.rest.dto.VotingResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/votes")
@Tag(name = "Vote", description = "Voting endpoints")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/session/{sessionId}")
    @Operation(summary = "Cast a vote", description = "Casts a vote in a voting session. CPF is validated and must be able to vote.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Vote cast successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate vote or session closed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Vote not allowed (CPF invalid or unable to vote)")
    })
    public ResponseEntity<VoteResponse> castVote(
            @PathVariable Long sessionId,
            @Valid @RequestBody CastVoteRequest request) {

        VoteResponse response = voteService.castVote(sessionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/session/{sessionId}/result")
    @Operation(summary = "Get voting result", description = "Retrieves the voting result for a session")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Result retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<VotingResultResponse> getVotingResult(@PathVariable Long sessionId) {
        VotingResultResponse response = voteService.getVotingResult(sessionId);
        return ResponseEntity.ok(response);
    }
}