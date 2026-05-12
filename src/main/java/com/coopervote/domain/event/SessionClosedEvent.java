package com.coopervote.domain.event;

public class SessionClosedEvent extends DomainEvent {

    private final Long sessionId;
    private final Long agendaId;
    private final long yesVotes;
    private final long noVotes;

    public SessionClosedEvent(Long sessionId, Long agendaId, long yesVotes, long noVotes) {
        super();
        this.sessionId = sessionId;
        this.agendaId = agendaId;
        this.yesVotes = yesVotes;
        this.noVotes = noVotes;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getAgendaId() {
        return agendaId;
    }

    public long getYesVotes() {
        return yesVotes;
    }

    public long getNoVotes() {
        return noVotes;
    }
}
