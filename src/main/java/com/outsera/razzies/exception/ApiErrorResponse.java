package com.outsera.razzies.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        @JsonProperty("timestamp")
        @JsonFormat(shape = JsonFormat.Shape.STRING) OffsetDateTime dataHora,
        @JsonProperty("status") int status,
        @JsonProperty("error") String erro,
        @JsonProperty("message") String mensagem,
        @JsonProperty("path") String caminho
) {
}
