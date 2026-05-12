package com.coopervote.application.service;

import com.coopervote.domain.model.VotingSession;
import com.coopervote.domain.repository.VotingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SessionExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(SessionExpirationScheduler.class);

    private final VotingSessionRepository votingSessionRepository;

    public SessionExpirationScheduler(VotingSessionRepository votingSessionRepository) {
        this.votingSessionRepository = votingSessionRepository;
    }

    @Scheduled(fixedRateString = "${session.expiration.check-interval:30000}")
    public void closeExpiredSessions() {
        List<VotingSession> expiredSessions = votingSessionRepository
                .findActiveSessionsExpiredBefore(LocalDateTime.now());

        for (VotingSession session : expiredSessions) {
            session.close();
            votingSessionRepository.save(session);
            log.info("Auto-closed expired session: {}", session.getId());
        }

        if (!expiredSessions.isEmpty()) {
            log.info("Closed {} expired sessions", expiredSessions.size());
        }
    }
}