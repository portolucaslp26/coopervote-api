package com.coopervote.presentation.rest.dto;

import com.coopervote.domain.model.Agenda;

public record AgendaResponse(
        Long id,
        String title,
        String description,
        String createdAt
) {
    public static AgendaResponse fromEntity(Agenda agenda) {
        return new AgendaResponse(
                agenda.getId(),
                agenda.getTitle(),
                agenda.getDescription(),
                agenda.getCreatedAt().toString()
        );
    }
}