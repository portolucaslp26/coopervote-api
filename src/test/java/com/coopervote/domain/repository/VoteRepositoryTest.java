package com.coopervote.domain.repository;

import com.coopervote.domain.model.Agenda;
import com.coopervote.domain.model.Vote;
import com.coopervote.domain.model.VotingSession;
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
class VoteRepositoryTest {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    private Agenda sampleAgenda;
    private VotingSession sampleSession;

    @BeforeEach
    void setUp() {
        voteRepository.deleteAll();
        votingSessionRepository.deleteAll();
        agendaRepository.deleteAll();

        sampleAgenda = new Agenda("Pauta de Teste", "Descricao");
        sampleAgenda = agendaRepository.save(sampleAgenda);

        sampleSession = new VotingSession(sampleAgenda, 60);
        sampleSession = votingSessionRepository.save(sampleSession);
    }

    @Nested
    @DisplayName("existsByVotingSessionIdAndAssociateCpf")
    class ExistsByVotingSessionIdAndAssociateCpf {

        @Test
        @DisplayName("should return true when vote exists")
        void shouldReturnTrueWhenVoteExists() {
            Vote vote = new Vote(sampleSession, "12345678900", true);
            voteRepository.save(vote);

            boolean exists = voteRepository.existsByVotingSessionIdAndAssociateCpf(sampleSession.getId(), "12345678900");

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("should return false when no vote exists")
        void shouldReturnFalseWhenNoVoteExists() {
            boolean exists = voteRepository.existsByVotingSessionIdAndAssociateCpf(sampleSession.getId(), "12345678900");

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("countYesVotes")
    class CountYesVotes {

        @Test
        @DisplayName("should count yes votes correctly")
        void shouldCountYesVotes() {
            Vote vote1 = new Vote(sampleSession, "11111111100", true);
            Vote vote2 = new Vote(sampleSession, "22222222200", true);
            Vote vote3 = new Vote(sampleSession, "33333333300", false);
            voteRepository.save(vote1);
            voteRepository.save(vote2);
            voteRepository.save(vote3);

            long count = voteRepository.countYesVotes(sampleSession.getId());

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("should return zero when no votes")
        void shouldReturnZeroWhenNoVotes() {
            long count = voteRepository.countYesVotes(sampleSession.getId());

            assertThat(count).isZero();
        }
    }

    @Nested
    @DisplayName("countNoVotes")
    class CountNoVotes {

        @Test
        @DisplayName("should count no votes correctly")
        void shouldCountNoVotes() {
            Vote vote1 = new Vote(sampleSession, "11111111100", false);
            Vote vote2 = new Vote(sampleSession, "22222222200", true);
            Vote vote3 = new Vote(sampleSession, "33333333300", false);
            voteRepository.save(vote1);
            voteRepository.save(vote2);
            voteRepository.save(vote3);

            long count = voteRepository.countNoVotes(sampleSession.getId());

            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("should save vote with all fields")
        void shouldSaveVoteWithAllFields() {
            Vote vote = new Vote(sampleSession, "12345678900", true);
            Vote saved = voteRepository.save(vote);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getAssociateCpf()).isEqualTo("12345678900");
            assertThat(saved.getVoteValue()).isTrue();
            assertThat(saved.getCreatedAt()).isNotNull();
        }
    }
}