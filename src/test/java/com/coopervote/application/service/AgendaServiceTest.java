package com.coopervote.application.service;

import com.coopervote.application.exception.AgendaNotFoundException;
import com.coopervote.domain.model.Agenda;
import com.coopervote.domain.repository.AgendaRepository;
import com.coopervote.presentation.rest.dto.AgendaResponse;
import com.coopervote.presentation.rest.dto.CreateAgendaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.coopervote.application.service.AgendaServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private AgendaServiceImpl agendaService;

    private Agenda sampleAgenda;

    @BeforeEach
    void setUp() {
        sampleAgenda = new Agenda("Pauta de Teste", "Descricao da pauta");
        setAgendaId(sampleAgenda, 1L);
    }

    private void setAgendaId(Agenda agenda, Long id) {
        try {
            java.lang.reflect.Field idField = Agenda.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(agenda, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("createAgenda")
    class CreateAgenda {

        @Test
        @DisplayName("should create agenda successfully")
        void shouldCreateAgendaSuccessfully() {
            CreateAgendaRequest request = new CreateAgendaRequest("Nova Pauta", "Nova Descricao");
            when(agendaRepository.save(any(Agenda.class))).thenAnswer(invocation -> {
                Agenda saved = invocation.getArgument(0);
                setAgendaId(saved, 1L);
                return saved;
            });

            AgendaResponse response = agendaService.createAgenda(request);

            assertThat(response).isNotNull();
            assertThat(response.title()).isEqualTo("Nova Pauta");
            assertThat(response.description()).isEqualTo("Nova Descricao");

            ArgumentCaptor<Agenda> agendaCaptor = ArgumentCaptor.forClass(Agenda.class);
            verify(agendaRepository).save(agendaCaptor.capture());
            Agenda captured = agendaCaptor.getValue();
            assertThat(captured.getTitle()).isEqualTo("Nova Pauta");
            assertThat(captured.getDescription()).isEqualTo("Nova Descricao");
        }

        @Test
        @DisplayName("should save agenda with current timestamp")
        void shouldSaveAgendaWithTimestamp() {
            CreateAgendaRequest request = new CreateAgendaRequest("Pauta", "Desc");
            when(agendaRepository.save(any(Agenda.class))).thenAnswer(invocation -> {
                Agenda saved = invocation.getArgument(0);
                setAgendaId(saved, 1L);
                return saved;
            });

            AgendaResponse response = agendaService.createAgenda(request);

            assertThat(response.createdAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("getAgenda")
    class GetAgenda {

        @Test
        @DisplayName("should return agenda when found")
        void shouldReturnAgendaWhenFound() {
            when(agendaRepository.findById(1L)).thenReturn(Optional.of(sampleAgenda));

            AgendaResponse response = agendaService.getAgenda(1L);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.title()).isEqualTo("Pauta de Teste");
        }

        @Test
        @DisplayName("should throw AgendaNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(agendaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> agendaService.getAgenda(999L))
                    .isInstanceOf(AgendaNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("listAllAgendas")
    class ListAllAgendas {

        @Test
        @DisplayName("should return all agendas")
        void shouldReturnAllAgendas() {
            Agenda agenda2 = new Agenda("Pauta 2", "Desc 2");
            setAgendaId(agenda2, 2L);
            Agenda agenda3 = new Agenda("Pauta 3", "Desc 3");
            setAgendaId(agenda3, 3L);

            when(agendaRepository.findAll()).thenReturn(Arrays.asList(sampleAgenda, agenda2, agenda3));

            List<AgendaResponse> responses = agendaService.listAllAgendas();

            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).title()).isEqualTo("Pauta de Teste");
            assertThat(responses.get(1).title()).isEqualTo("Pauta 2");
            assertThat(responses.get(2).title()).isEqualTo("Pauta 3");
        }

        @Test
        @DisplayName("should return empty list when no agendas")
        void shouldReturnEmptyListWhenNoAgendas() {
            when(agendaRepository.findAll()).thenReturn(List.of());

            List<AgendaResponse> responses = agendaService.listAllAgendas();

            assertThat(responses).isEmpty();
        }
    }
}