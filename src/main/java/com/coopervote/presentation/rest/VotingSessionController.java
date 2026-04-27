package com.coopervote.presentation.rest;

import com.coopervote.application.service.VotingSessionService;
import com.coopervote.presentation.rest.dto.OpenSessionRequest;
import com.coopervote.presentation.rest.dto.VotingSessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@Tag(name = "Voting Session", description = "Voting session management endpoints")
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    public VotingSessionController(VotingSessionService votingSessionService) {
        this.votingSessionService = votingSessionService;
    }

    @PostMapping("/agenda/{agendaId}")
    @Operation(summary = "Open a voting session", description = "Opens a new voting session for an agenda")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Session opened successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Agenda not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Session already exists")
    })
    public ResponseEntity<VotingSessionResponse> openSession(
            @PathVariable Long agendaId,
            @Valid @RequestBody(required = false) OpenSessionRequest request) {
        VotingSessionResponse response = votingSessionService.openSession(agendaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session by ID", description = "Retrieves a voting session by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<VotingSessionResponse> getSession(@PathVariable Long id) {
        VotingSessionResponse response = votingSessionService.getSession(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agenda/{agendaId}")
    @Operation(summary = "Get session by agenda ID", description = "Retrieves the active voting session for an agenda")
    public ResponseEntity<VotingSessionResponse> getSessionByAgendaId(@PathVariable Long agendaId) {
        VotingSessionResponse response = votingSessionService.getSessionByAgendaId(agendaId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close a voting session", description = "Closes an active voting session")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session closed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<VotingSessionResponse> closeSession(@PathVariable Long id) {
        VotingSessionResponse response = votingSessionService.closeSession(id);
        return ResponseEntity.ok(response);
    }
}