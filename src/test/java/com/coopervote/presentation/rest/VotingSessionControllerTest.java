package com.coopervote.presentation.rest;

import com.coopervote.application.exception.AgendaNotFoundException;
import com.coopervote.application.exception.SessionAlreadyExistsException;
import com.coopervote.application.exception.SessionNotFoundException;
import com.coopervote.application.service.VotingSessionService;
import com.coopervote.presentation.rest.dto.OpenSessionRequest;
import com.coopervote.presentation.rest.dto.VotingSessionResponse;
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

@WebMvcTest(VotingSessionController.class)
class VotingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VotingSessionService votingSessionService;

    @Nested
    @DisplayName("POST /api/v1/sessions/agenda/{agendaId}")
    class OpenSession {

        @Test
        @DisplayName("should open session with default duration")
        void shouldOpenSessionWithDefaultDuration() throws Exception {
            OpenSessionRequest request = new OpenSessionRequest(null);
            VotingSessionResponse response = new VotingSessionResponse(
                    1L, 1L, "Pauta de Teste", "2024-01-01T10:00:00", "2024-01-01T10:01:00", true
            );

            when(votingSessionService.openSession(eq(1L), any(OpenSessionRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/v1/sessions/agenda/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.agendaId").value(1))
                    .andExpect(jsonPath("$.isActive").value(true));
        }

        @Test
        @DisplayName("should open session with custom duration")
        void shouldOpenSessionWithCustomDuration() throws Exception {
            OpenSessionRequest request = new OpenSessionRequest(30);
            VotingSessionResponse response = new VotingSessionResponse(
                    1L, 1L, "Pauta de Teste", "2024-01-01T10:00:00", "2024-01-01T10:30:00", true
            );

            when(votingSessionService.openSession(eq(1L), any(OpenSessionRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/v1/sessions/agenda/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("should return not found when agenda not found")
        void shouldReturnNotFoundWhenAgendaNotFound() throws Exception {
            OpenSessionRequest request = new OpenSessionRequest(null);
            when(votingSessionService.openSession(eq(999L), any(OpenSessionRequest.class)))
                    .thenThrow(new AgendaNotFoundException(999L));

            mockMvc.perform(post("/api/v1/sessions/agenda/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return conflict when session already exists")
        void shouldReturnConflictWhenSessionAlreadyExists() throws Exception {
            OpenSessionRequest request = new OpenSessionRequest(null);
            when(votingSessionService.openSession(eq(1L), any(OpenSessionRequest.class)))
                    .thenThrow(new SessionAlreadyExistsException(1L));

            mockMvc.perform(post("/api/v1/sessions/agenda/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/sessions/{id}")
    class GetSession {

        @Test
        @DisplayName("should return session when found")
        void shouldReturnSessionWhenFound() throws Exception {
            VotingSessionResponse response = new VotingSessionResponse(
                    1L, 1L, "Pauta de Teste", "2024-01-01T10:00:00", "2024-01-01T11:00:00", true
            );
            when(votingSessionService.getSession(1L)).thenReturn(response);

            mockMvc.perform(get("/api/v1/sessions/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("should return not found when session not found")
        void shouldReturnNotFoundWhenSessionNotFound() throws Exception {
            when(votingSessionService.getSession(999L)).thenThrow(new SessionNotFoundException(999L));

            mockMvc.perform(get("/api/v1/sessions/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/sessions/agenda/{agendaId}")
    class GetSessionByAgendaId {

        @Test
        @DisplayName("should return session when found by agenda id")
        void shouldReturnSessionByAgendaId() throws Exception {
            VotingSessionResponse response = new VotingSessionResponse(
                    1L, 1L, "Pauta de Teste", "2024-01-01T10:00:00", "2024-01-01T11:00:00", true
            );
            when(votingSessionService.getSessionByAgendaId(1L)).thenReturn(response);

            mockMvc.perform(get("/api/v1/sessions/agenda/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.agendaId").value(1));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/sessions/{id}/close")
    class CloseSession {

        @Test
        @DisplayName("should close session successfully")
        void shouldCloseSessionSuccessfully() throws Exception {
            VotingSessionResponse response = new VotingSessionResponse(
                    1L, 1L, "Pauta de Teste", "2024-01-01T10:00:00", "2024-01-01T11:00:00", false
            );
            when(votingSessionService.closeSession(1L)).thenReturn(response);

            mockMvc.perform(post("/api/v1/sessions/1/close"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isActive").value(false));
        }

        @Test
        @DisplayName("should return not found when session not found")
        void shouldReturnNotFoundWhenSessionNotFound() throws Exception {
            when(votingSessionService.closeSession(999L)).thenThrow(new SessionNotFoundException(999L));

            mockMvc.perform(post("/api/v1/sessions/999/close"))
                    .andExpect(status().isNotFound());
        }
    }
}