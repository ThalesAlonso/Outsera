package com.outsera.razzies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AwardIntervalResponse(
        @JsonProperty("min") List<AwardIntervalItemResponse> minimo,
        @JsonProperty("max") List<AwardIntervalItemResponse> maximo
) {
}
