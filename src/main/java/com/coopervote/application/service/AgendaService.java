package com.coopervote.application.service;

import com.coopervote.presentation.rest.dto.AgendaResponse;
import com.coopervote.presentation.rest.dto.CreateAgendaRequest;

import java.util.List;

public interface AgendaService {

    AgendaResponse createAgenda(CreateAgendaRequest request);

    AgendaResponse getAgenda(Long id);

    List<AgendaResponse> listAllAgendas();
}
