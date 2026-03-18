package com.outsera.razzies.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> tratarRecursoNaoEncontrado(
            NoResourceFoundException excecao,
            HttpServletRequest requisicao
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse resposta = new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Recurso nao encontrado.",
                requisicao.getRequestURI()
        );
        return ResponseEntity.status(status).body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarExcecaoInesperada(
            Exception excecao,
            HttpServletRequest requisicao
    ) {
        LOGGER.error("Erro inesperado ao processar requisicao {}", requisicao.getRequestURI(), excecao);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorResponse resposta = new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Ocorreu um erro inesperado.",
                requisicao.getRequestURI()
        );
        return ResponseEntity.status(status).body(resposta);
    }
}
