package com.coopervote.application.exception;

public class AgendaNotFoundException extends RuntimeException {

    private final Long agendaId;

    public AgendaNotFoundException(Long agendaId) {
        super("Pauta nao encontrada com ID: " + agendaId);
        this.agendaId = agendaId;
    }

    public Long getAgendaId() {
        return agendaId;
    }
}