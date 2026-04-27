package com.coopervote.infrastructure.cpf;

public class InvalidCpfException extends RuntimeException {

    private final String cpf;

    public InvalidCpfException(String cpf) {
        super("CPF invalido: " + cpf);
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }
}