package com.coopervote.presentation.rest;

import com.coopervote.application.exception.AgendaNotFoundException;
import com.coopervote.application.service.AgendaService;
import com.coopervote.presentation.rest.dto.AgendaResponse;
import com.coopervote.presentation.rest.dto.CreateAgendaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendaController.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgendaService agendaService;

    @Nested
    @DisplayName("POST /api/v1/agendas")
    class CreateAgenda {

        @Test
        @DisplayName("should create agenda with valid request")
        void shouldCreateAgendaWithValidRequest() throws Exception {
            CreateAgendaRequest request = new CreateAgendaRequest("Nova Pauta", "Descricao");
            AgendaResponse response = new AgendaResponse(1L, "Nova Pauta", "Descricao", "2024-01-01T10:00:00");

            when(agendaService.createAgenda(any(CreateAgendaRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/v1/agendas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Nova Pauta"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/agendas/{id}")
    class GetAgenda {

        @Test
        @DisplayName("should return agenda when found")
        void shouldReturnAgendaWhenFound() throws Exception {
            AgendaResponse response = new AgendaResponse(1L, "Pauta", "Desc", "2024-01-01T10:00:00");
            when(agendaService.getAgenda(1L)).thenReturn(response);

            mockMvc.perform(get("/api/v1/agendas/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Pauta"));
        }

        @Test
        @DisplayName("should return not found when agenda not found")
        void shouldReturnNotFoundWhenNotFound() throws Exception {
            when(agendaService.getAgenda(999L)).thenThrow(new AgendaNotFoundException(999L));

            mockMvc.perform(get("/api/v1/agendas/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/agendas")
    class ListAgendas {

        @Test
        @DisplayName("should return all agendas")
        void shouldReturnAllAgendas() throws Exception {
            List<AgendaResponse> responses = Arrays.asList(
                    new AgendaResponse(1L, "Pauta 1", "Desc 1", "2024-01-01T10:00:00"),
                    new AgendaResponse(2L, "Pauta 2", "Desc 2", "2024-01-01T10:00:00")
            );
            when(agendaService.listAllAgendas()).thenReturn(responses);

            mockMvc.perform(get("/api/v1/agendas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value("Pauta 1"))
                    .andExpect(jsonPath("$[1].title").value("Pauta 2"));
        }
    }
}