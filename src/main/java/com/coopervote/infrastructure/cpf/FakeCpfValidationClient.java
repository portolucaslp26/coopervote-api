package com.coopervote.infrastructure.cpf;

import org.springframework.stereotype.Component;

@Component
public class FakeCpfValidationClient implements CpfValidationClient {

    @Override
    public CpfStatus validate(String cpf) {
        if (cpf == null || cpf.length() != 11 || !cpf.matches("\\d{11}")) {
            throw new InvalidCpfException(cpf);
        }
        
        return CpfStatus.ABLE_TO_VOTE;
    }
}