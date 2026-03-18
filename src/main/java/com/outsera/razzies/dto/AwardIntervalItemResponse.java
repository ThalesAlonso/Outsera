package com.outsera.razzies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AwardIntervalItemResponse(
        @JsonProperty("producer") String produtor,
        @JsonProperty("interval") int intervalo,
        @JsonProperty("previousWin") int vitoriaAnterior,
        @JsonProperty("followingWin") int vitoriaSeguinte
) {
}
