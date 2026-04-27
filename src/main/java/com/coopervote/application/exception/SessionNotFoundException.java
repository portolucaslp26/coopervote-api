package com.coopervote.application.exception;

public class SessionNotFoundException extends RuntimeException {

    private final Long sessionId;

    public SessionNotFoundException(Long sessionId) {
        super("Sessao de votacao nao encontrada com ID: " + sessionId);
        this.sessionId = sessionId;
    }

    public Long getSessionId() {
        return sessionId;
    }
}