package com.coopervote.domain.repository;

import com.coopervote.domain.model.Agenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AgendaRepositoryTest {

    @Autowired
    private AgendaRepository agendaRepository;

    @BeforeEach
    void setUp() {
        agendaRepository.deleteAll();
    }

    @Nested
    @DisplayName("findByTitle")
    class FindByTitle {

        @Test
        @DisplayName("should return agenda when found by title")
        void shouldReturnAgendaWhenFound() {
            Agenda agenda = new Agenda("Pauta de Teste", "Descricao");
            agendaRepository.save(agenda);

            Optional<Agenda> found = agendaRepository.findByTitle("Pauta de Teste");

            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("Pauta de Teste");
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            Optional<Agenda> found = agendaRepository.findByTitle("Nao Existe");

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("should save agenda with all fields")
        void shouldSaveAgendaWithAllFields() {
            Agenda agenda = new Agenda("Nova Pauta", "Nova Descricao");
            Agenda saved = agendaRepository.save(agenda);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getTitle()).isEqualTo("Nova Pauta");
            assertThat(saved.getDescription()).isEqualTo("Nova Descricao");
            assertThat(saved.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all agendas")
        void shouldReturnAllAgendas() {
            Agenda agenda1 = new Agenda("Pauta 1", "Desc 1");
            Agenda agenda2 = new Agenda("Pauta 2", "Desc 2");
            agendaRepository.save(agenda1);
            agendaRepository.save(agenda2);

            var all = agendaRepository.findAll();

            assertThat(all).hasSize(2);
        }
    }
}