CREATE TABLE agenda (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_agenda_title UNIQUE (title)
);

CREATE TABLE voting_session (
    id BIGSERIAL PRIMARY KEY,
    agenda_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_voting_session_agenda FOREIGN KEY (agenda_id) REFERENCES agenda(id) ON DELETE CASCADE
);

CREATE TABLE vote (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    associate_cpf VARCHAR(11) NOT NULL,
    vote_value BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_vote_session FOREIGN KEY (session_id) REFERENCES voting_session(id) ON DELETE CASCADE,
    CONSTRAINT uk_vote_cpf_session UNIQUE (associate_cpf, session_id)
);

CREATE INDEX idx_vote_session_id ON vote(session_id);
CREATE INDEX idx_vote_cpf ON vote(associate_cpf);
CREATE INDEX idx_voting_session_agenda_id ON voting_session(agenda_id);
CREATE INDEX idx_voting_session_active ON voting_session(is_active);