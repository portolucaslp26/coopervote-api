package com.coopervote.presentation.rest.dto;

public record CreateAgendaRequest(
        String title,
        String description
) {}