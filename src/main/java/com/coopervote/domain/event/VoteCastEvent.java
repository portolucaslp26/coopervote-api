package com.coopervote.domain.event;

public class VoteCastEvent extends DomainEvent {

    private final Long sessionId;
    private final String associateCpf;
    private final Boolean voteValue;

    public VoteCastEvent(Long sessionId, String associateCpf, Boolean voteValue) {
        super();
        this.sessionId = sessionId;
        this.associateCpf = associateCpf;
        this.voteValue = voteValue;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getAssociateCpf() {
        return associateCpf;
    }

    public Boolean getVoteValue() {
        return voteValue;
    }
}
