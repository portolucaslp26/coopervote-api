package com.coopervote.application.exception;

public class VoteNotAllowedException extends RuntimeException {

    private final String cpf;
    private final String reason;

    public VoteNotAllowedException(String cpf, String reason) {
        super("Voto nao permitido para CPF " + cpf + ": " + reason);
        this.cpf = cpf;
        this.reason = reason;
    }

    public String getCpf() {
        return cpf;
    }

    public String getReason() {
        return reason;
    }
}