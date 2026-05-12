package com.coopervote.domain.event;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
