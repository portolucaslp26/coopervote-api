package com.coopervote.infrastructure.cpf;

public interface CpfValidationClient {
    CpfStatus validate(String cpf);
}