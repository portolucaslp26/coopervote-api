package com.coopervote.presentation.rest;

import com.coopervote.application.exception.AgendaNotFoundException;
import com.coopervote.application.exception.DuplicateVoteException;
import com.coopervote.application.exception.SessionAlreadyExistsException;
import com.coopervote.application.exception.SessionClosedException;
import com.coopervote.application.exception.SessionNotFoundException;
import com.coopervote.application.exception.VoteNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AgendaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAgendaNotFound(AgendaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("AGENDA_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("SESSION_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(SessionClosedException.class)
    public ResponseEntity<ErrorResponse> handleSessionClosed(SessionClosedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("SESSION_CLOSED", ex.getMessage()));
    }

    @ExceptionHandler(SessionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSessionAlreadyExists(SessionAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("SESSION_ALREADY_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateVoteException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateVote(DuplicateVoteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("DUPLICATE_VOTE", ex.getMessage()));
    }

    @ExceptionHandler(VoteNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleVoteNotAllowed(VoteNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse("VOTE_NOT_ALLOWED", ex.getMessage()));
    }

    public record ErrorResponse(String code, String message) {}
}