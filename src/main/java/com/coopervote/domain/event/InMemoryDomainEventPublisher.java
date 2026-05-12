package com.coopervote.domain.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InMemoryDomainEventPublisher.class);

    @Override
    public void publish(DomainEvent event) {
        if (event instanceof VoteCastEvent voteEvent) {
            log.info("VoteCastEvent published - Session: {}, CPF: {}, Value: {}",
                    voteEvent.getSessionId(),
                    maskCpf(voteEvent.getAssociateCpf()),
                    voteEvent.getVoteValue());
        } else if (event instanceof SessionOpenedEvent sessionEvent) {
            log.info("SessionOpenedEvent published - Session: {}, Agenda: {}",
                    sessionEvent.getSessionId(),
                    sessionEvent.getAgendaId());
        } else if (event instanceof SessionClosedEvent closedEvent) {
            log.info("SessionClosedEvent published - Session: {}, Agenda: {}, Yes: {}, No: {}",
                    closedEvent.getSessionId(),
                    closedEvent.getAgendaId(),
                    closedEvent.getYesVotes(),
                    closedEvent.getNoVotes());
        } else {
            log.info("DomainEvent published: {}", event.getClass().getSimpleName());
        }
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "****";
        }
        return "***." + cpf.substring(cpf.length() - 4);
    }
}
