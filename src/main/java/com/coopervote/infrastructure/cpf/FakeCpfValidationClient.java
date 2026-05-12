package com.coopervote.infrastructure.cpf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class FakeCpfValidationClient implements CpfValidationClient {

    private static final Logger log = LoggerFactory.getLogger(FakeCpfValidationClient.class);

    private final ExecutorService executor;
    private final long timeoutMillis;

    public FakeCpfValidationClient(
            @Value("${cpf.validation.timeout.millis:3000}") long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        this.executor = Executors.newFixedThreadPool(10);
    }

    @Override
    public CpfStatus validate(String cpf) {
        try {
            Future<CpfStatus> future = executor.submit(() -> doValidate(cpf));
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.error("CPF validation timeout after {}ms for CPF ending in: ****{}", 
                    timeoutMillis, cpf != null && cpf.length() > 4 ? cpf.substring(cpf.length() - 4) : "****");
            throw new CpfValidationTimeoutException("CPF validation timed out", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CpfValidationTimeoutException("CPF validation interrupted", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new CpfValidationTimeoutException("CPF validation failed", e);
        }
    }

    private CpfStatus doValidate(String cpf) {
        if (cpf == null || cpf.length() != 11 || !cpf.matches("\\d{11}")) {
            throw new InvalidCpfException(cpf);
        }
        return CpfStatus.ABLE_TO_VOTE;
    }
}
