package com.coopervote.presentation.rest;

import com.coopervote.application.service.AgendaService;
import com.coopervote.presentation.rest.dto.AgendaResponse;
import com.coopervote.presentation.rest.dto.CreateAgendaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agendas")
@Tag(name = "Agenda", description = "Agenda management endpoints")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping
    @Operation(summary = "Create a new agenda", description = "Creates a new voting agenda")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Agenda created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<AgendaResponse> createAgenda(@Valid @RequestBody CreateAgendaRequest request) {
        AgendaResponse response = agendaService.createAgenda(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get agenda by ID", description = "Retrieves an agenda by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Agenda found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Agenda not found")
    })
    public ResponseEntity<AgendaResponse> getAgenda(@PathVariable Long id) {
        AgendaResponse response = agendaService.getAgenda(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all agendas", description = "Retrieves all available agendas")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of agendas retrieved")
    })
    public ResponseEntity<List<AgendaResponse>> listAgendas() {
        List<AgendaResponse> responses = agendaService.listAllAgendas();
        return ResponseEntity.ok(responses);
    }
}