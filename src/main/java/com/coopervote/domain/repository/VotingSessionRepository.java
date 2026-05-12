package com.coopervote.domain.repository;

import com.coopervote.domain.model.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    boolean existsByAgendaId(Long agendaId);

    Optional<VotingSession> findByAgendaId(Long agendaId);

    @Query("SELECT vs FROM VotingSession vs JOIN FETCH vs.agenda WHERE vs.id = :id")
    Optional<VotingSession> findByIdWithAgenda(Long id);

    @Query("SELECT vs FROM VotingSession vs WHERE vs.isActive = true AND vs.endTime < :now")
    List<VotingSession> findActiveSessionsExpiredBefore(LocalDateTime now);
}