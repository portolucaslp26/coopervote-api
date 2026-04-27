package com.coopervote.application.exception;

public class SessionAlreadyExistsException extends RuntimeException {

    private final Long agendaId;

    public SessionAlreadyExistsException(Long agendaId) {
        super("Ja existe uma sessao de votacao para esta pauta: " + agendaId);
        this.agendaId = agendaId;
    }

    public Long getAgendaId() {
        return agendaId;
    }
}