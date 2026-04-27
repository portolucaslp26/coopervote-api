package com.coopervote.presentation.rest.dto;

public record OpenSessionRequest(
        Integer durationMinutes
) {}