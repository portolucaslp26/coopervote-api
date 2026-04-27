package com.coopervote.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "vote", uniqueConstraints = {
        @UniqueConstraint(name = "uk_vote_cpf_session", columnNames = {"associate_cpf", "session_id"})
}, indexes = {
        @Index(name = "idx_vote_session", columnList = "session_id")
})
@Getter
@NoArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private VotingSession votingSession;

    @Column(name = "associate_cpf", nullable = false, length = 11)
    private String associateCpf;

    @Column(name = "vote_value", nullable = false)
    private Boolean voteValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Vote(VotingSession votingSession, String associateCpf, Boolean voteValue) {
        this.votingSession = votingSession;
        this.associateCpf = associateCpf;
        this.voteValue = voteValue;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isYes() {
        return Boolean.TRUE.equals(voteValue);
    }

    public boolean isNo() {
        return Boolean.FALSE.equals(voteValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(id, vote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    void setVotingSession(VotingSession votingSession) {
        this.votingSession = votingSession;
    }
}