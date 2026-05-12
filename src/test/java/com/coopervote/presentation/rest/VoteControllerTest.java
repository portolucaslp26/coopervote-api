package com.coopervote.presentation.rest;

import com.coopervote.application.exception.DuplicateVoteException;
import com.coopervote.application.exception.SessionClosedException;
import com.coopervote.application.exception.SessionNotFoundException;
import com.coopervote.application.exception.VoteNotAllowedException;
import com.coopervote.application.service.VoteService;
import com.coopervote.presentation.rest.dto.CastVoteRequest;
import com.coopervote.presentation.rest.dto.VoteResponse;
import com.coopervote.presentation.rest.dto.VotingResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VoteService voteService;

    @Nested
    @DisplayName("POST /api/v1/votes/session/{sessionId}")
    class CastVote {

        @Test
        @DisplayName("should cast vote successfully")
        void shouldCastVoteSuccessfully() throws Exception {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            VoteResponse response = new VoteResponse(1L, 1L, "12345678900", true, "2024-01-01T10:00:00");

            when(voteService.castVote(eq(1L), any(CastVoteRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/v1/votes/session/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.voteValue").value(true));
        }

        @Test
        @DisplayName("should return not found when session not found")
        void shouldReturnNotFoundWhenSessionNotFound() throws Exception {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(voteService.castVote(eq(999L), any(CastVoteRequest.class)))
                    .thenThrow(new SessionNotFoundException(999L));

            mockMvc.perform(post("/api/v1/votes/session/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return conflict when session is closed")
        void shouldReturnConflictWhenSessionClosed() throws Exception {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(voteService.castVote(eq(1L), any(CastVoteRequest.class)))
                    .thenThrow(new SessionClosedException(1L));

            mockMvc.perform(post("/api/v1/votes/session/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("should return unprocessable when CPF not allowed to vote")
        void shouldReturnUnprocessableWhenCpfNotAllowed() throws Exception {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(voteService.castVote(eq(1L), any(CastVoteRequest.class)))
                    .thenThrow(new VoteNotAllowedException("12345678900", "CPF invalid"));

            mockMvc.perform(post("/api/v1/votes/session/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return conflict on duplicate vote")
        void shouldReturnConflictOnDuplicateVote() throws Exception {
            CastVoteRequest request = new CastVoteRequest("12345678900", true);
            when(voteService.castVote(eq(1L), any(CastVoteRequest.class)))
                    .thenThrow(new DuplicateVoteException("12345678900", 1L));

            mockMvc.perform(post("/api/v1/votes/session/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/votes/session/{sessionId}/result")
    class GetVotingResult {

        @Test
        @DisplayName("should return voting result")
        void shouldReturnVotingResult() throws Exception {
            VotingResultResponse response = new VotingResultResponse(1L, 1L, 5L, 3L, 8L, "APROVADO");
            when(voteService.getVotingResult(1L)).thenReturn(response);

            mockMvc.perform(get("/api/v1/votes/session/1/result"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sessionId").value(1))
                    .andExpect(jsonPath("$.yesVotes").value(5))
                    .andExpect(jsonPath("$.noVotes").value(3));
        }

        @Test
        @DisplayName("should return not found when session not found")
        void shouldReturnNotFoundWhenSessionNotFound() throws Exception {
            when(voteService.getVotingResult(999L)).thenThrow(new SessionNotFoundException(999L));

            mockMvc.perform(get("/api/v1/votes/session/999/result"))
                    .andExpect(status().isNotFound());
        }
    }
}