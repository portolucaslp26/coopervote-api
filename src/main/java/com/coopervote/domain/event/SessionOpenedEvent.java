package com.coopervote.domain.event;

public class SessionOpenedEvent extends DomainEvent {

    private final Long sessionId;
    private final Long agendaId;

    public SessionOpenedEvent(Long sessionId, Long agendaId) {
        super();
        this.sessionId = sessionId;
        this.agendaId = agendaId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getAgendaId() {
        return agendaId;
    }
}
