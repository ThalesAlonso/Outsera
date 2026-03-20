package com.outsera.razzies.service;

public record ProducerAwardInterval(
        String produtor,
        int intervalo,
        int vitoriaAnterior,
        int vitoriaSeguinte
) {
}
