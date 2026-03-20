package com.outsera.razzies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Intervalo entre duas vitorias consecutivas de um produtor")
public record AwardIntervalItemResponse(
        @Schema(description = "Nome do produtor") @JsonProperty("producer") String produtor,
        @Schema(description = "Intervalo em anos entre as duas vitorias") @JsonProperty("interval") int intervalo,
        @Schema(description = "Ano da vitoria anterior") @JsonProperty("previousWin") int vitoriaAnterior,
        @Schema(description = "Ano da vitoria seguinte") @JsonProperty("followingWin") int vitoriaSeguinte
) {
}
