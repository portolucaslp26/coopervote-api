package com.coopervote.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter voteCastCounter(MeterRegistry registry) {
        return Counter.builder("coopervote.votes.cast")
                .description("Total number of votes cast")
                .register(registry);
    }

    @Bean
    public Counter voteRejectedCounter(MeterRegistry registry) {
        return Counter.builder("coopervote.votes.rejected")
                .description("Total number of rejected votes")
                .register(registry);
    }

    @Bean
    public Timer voteProcessingTimer(MeterRegistry registry) {
        return Timer.builder("coopervote.votes.processing.time")
                .description("Time taken to process a vote")
                .register(registry);
    }

    @Bean
    public Counter sessionOpenedCounter(MeterRegistry registry) {
        return Counter.builder("coopervote.sessions.opened")
                .description("Total number of voting sessions opened")
                .register(registry);
    }

    @Bean
    public Counter sessionClosedCounter(MeterRegistry registry) {
        return Counter.builder("coopervote.sessions.closed")
                .description("Total number of voting sessions closed")
                .register(registry);
    }

    @Bean
    public Counter agendaCreatedCounter(MeterRegistry registry) {
        return Counter.builder("coopervote.agendas.created")
                .description("Total number of agendas created")
                .register(registry);
    }
}
