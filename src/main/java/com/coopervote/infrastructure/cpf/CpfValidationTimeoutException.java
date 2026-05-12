package com.coopervote.infrastructure.cpf;

public class CpfValidationTimeoutException extends RuntimeException {

    public CpfValidationTimeoutException(String message) {
        super(message);
    }

    public CpfValidationTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
