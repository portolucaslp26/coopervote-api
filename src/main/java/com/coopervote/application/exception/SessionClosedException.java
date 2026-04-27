package com.coopervote.application.exception;

public class SessionClosedException extends RuntimeException {

    private final Long sessionId;

    public SessionClosedException(Long sessionId) {
        super("Sessao de votacao encerrada: " + sessionId);
        this.sessionId = sessionId;
    }

    public Long getSessionId() {
        return sessionId;
    }
}