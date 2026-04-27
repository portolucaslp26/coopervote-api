package com.coopervote.application.exception;

public class DuplicateVoteException extends RuntimeException {

    private final String cpf;
    private final Long sessionId;

    public DuplicateVoteException(String cpf, Long sessionId) {
        super("Associado com CPF " + cpf + " ja votou nesta sessao (" + sessionId + ")");
        this.cpf = cpf;
        this.sessionId = sessionId;
    }

    public String getCpf() {
        return cpf;
    }

    public Long getSessionId() {
        return sessionId;
    }
}