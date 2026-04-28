package com.coopervote.application.service;

import com.coopervote.application.exception.AgendaNotFoundException;
import com.coopervote.domain.model.Agenda;
import com.coopervote.domain.repository.AgendaRepository;
import com.coopervote.presentation.rest.dto.AgendaResponse;
import com.coopervote.presentation.rest.dto.CreateAgendaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AgendaServiceImpl implements AgendaService {

    private static final Logger log = LoggerFactory.getLogger(AgendaServiceImpl.class);

    private final AgendaRepository agendaRepository;

    public AgendaServiceImpl(AgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    @Override
    public AgendaResponse createAgenda(CreateAgendaRequest request) {
        log.info("Creating new agenda with title: {}", request.title());

        Agenda agenda = new Agenda(request.title(), request.description());
        Agenda saved = agendaRepository.save(agenda);

        log.info("Agenda created successfully with id: {}", saved.getId());
        return AgendaResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AgendaResponse getAgenda(Long id) {
        log.debug("Fetching agenda with id: {}", id);

        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new AgendaNotFoundException(id));

        return AgendaResponse.fromEntity(agenda);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgendaResponse> listAllAgendas() {
        log.debug("Fetching all agendas");

        return agendaRepository.findAll().stream()
                .map(AgendaResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
