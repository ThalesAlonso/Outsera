package com.outsera.razzies.service;

import java.util.List;

public record ProducerAwardIntervalResult(
        List<ProducerAwardInterval> minimo,
        List<ProducerAwardInterval> maximo
) {
}
