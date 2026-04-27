package com.coopervote.domain.repository;

import com.coopervote.domain.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVotingSessionIdAndAssociateCpf(Long sessionId, String cpf);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.votingSession.id = :sessionId AND v.voteValue = true")
    long countYesVotes(Long sessionId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.votingSession.id = :sessionId AND v.voteValue = false")
    long countNoVotes(Long sessionId);
}