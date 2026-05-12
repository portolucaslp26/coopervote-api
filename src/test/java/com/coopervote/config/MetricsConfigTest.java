package com.coopervote.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsConfigTest {

    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final MetricsConfig metricsConfig = new MetricsConfig();

    @Test
    @DisplayName("should create MetricsConfig instance")
    void shouldCreateMetricsConfigInstance() {
        assertThat(metricsConfig).isNotNull();
    }

    @Test
    @DisplayName("voteCastCounter should be created and registered")
    void voteCastCounterShouldBeCreatedAndRegistered() {
        Counter counter = metricsConfig.voteCastCounter(meterRegistry);

        assertThat(counter).isNotNull();
        assertThat(meterRegistry.find("coopervote.votes.cast").counter()).isNotNull();
    }

    @Test
    @DisplayName("voteRejectedCounter should be created and registered")
    void voteRejectedCounterShouldBeCreatedAndRegistered() {
        Counter counter = metricsConfig.voteRejectedCounter(meterRegistry);

        assertThat(counter).isNotNull();
        assertThat(meterRegistry.find("coopervote.votes.rejected").counter()).isNotNull();
    }

    @Test
    @DisplayName("sessionOpenedCounter should be created and registered")
    void sessionOpenedCounterShouldBeCreatedAndRegistered() {
        Counter counter = metricsConfig.sessionOpenedCounter(meterRegistry);

        assertThat(counter).isNotNull();
        assertThat(meterRegistry.find("coopervote.sessions.opened").counter()).isNotNull();
    }

    @Test
    @DisplayName("sessionClosedCounter should be created and registered")
    void sessionClosedCounterShouldBeCreatedAndRegistered() {
        Counter counter = metricsConfig.sessionClosedCounter(meterRegistry);

        assertThat(counter).isNotNull();
        assertThat(meterRegistry.find("coopervote.sessions.closed").counter()).isNotNull();
    }

    @Test
    @DisplayName("agendaCreatedCounter should be created and registered")
    void agendaCreatedCounterShouldBeCreatedAndRegistered() {
        Counter counter = metricsConfig.agendaCreatedCounter(meterRegistry);

        assertThat(counter).isNotNull();
        assertThat(meterRegistry.find("coopervote.agendas.created").counter()).isNotNull();
    }
}
