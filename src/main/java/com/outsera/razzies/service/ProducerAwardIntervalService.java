package com.outsera.razzies.service;

import com.outsera.razzies.entity.MovieEntity;
import com.outsera.razzies.entity.ProducerEntity;
import com.outsera.razzies.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProducerAwardIntervalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerAwardIntervalService.class);
    private static final Comparator<ProducerAwardInterval> ORDENACAO_INTERVALO =
            Comparator.comparing(ProducerAwardInterval::produtor)
                    .thenComparingInt(ProducerAwardInterval::vitoriaAnterior)
                    .thenComparingInt(ProducerAwardInterval::vitoriaSeguinte);

    private final MovieRepository repositorioFilme;

    public ProducerAwardIntervalService(MovieRepository repositorioFilme) {
        this.repositorioFilme = repositorioFilme;
    }

    @Transactional(readOnly = true)
    public ProducerAwardIntervalResult buscarIntervalosPremiacao() {
        List<MovieEntity> filmesVencedores = repositorioFilme.findByVencedorTrueOrderByAnoPremiacaoAscTituloAsc();
        Map<String, Integer> ultimaVitoriaPorProdutor = new HashMap<>();
        IntervalAccumulator acumulador = new IntervalAccumulator();

        for (MovieEntity filme : filmesVencedores) {
            for (ProducerEntity produtor : filme.getProdutores()) {
                processarVitoriaProdutor(
                        produtor.getNome(),
                        filme.getAnoPremiacao(),
                        ultimaVitoriaPorProdutor,
                        acumulador
                );
            }
        }

        if (!acumulador.possuiIntervalos()) {
            LOGGER.info("Nenhum produtor possui vitorias suficientes para gerar intervalo");
            return new ProducerAwardIntervalResult(List.of(), List.of());
        }

        ProducerAwardIntervalResult resultado = acumulador.paraResultado();
        LOGGER.info(
                "Intervalos de premiacao calculados: intervaloMinimo={}, intervaloMaximo={}",
                acumulador.intervaloMinimo,
                acumulador.intervaloMaximo
        );
        return resultado;
    }

    private void processarVitoriaProdutor(
            String nomeProdutor,
            int anoPremiacao,
            Map<String, Integer> ultimaVitoriaPorProdutor,
            IntervalAccumulator acumulador
    ) {
        Integer ultimaVitoria = ultimaVitoriaPorProdutor.put(nomeProdutor, anoPremiacao);
        if (ultimaVitoria == null) {
            return;
        }

        ProducerAwardInterval intervalo = new ProducerAwardInterval(
                nomeProdutor,
                anoPremiacao - ultimaVitoria,
                ultimaVitoria,
                anoPremiacao
        );
        acumulador.registrar(intervalo);
    }

    private static final class IntervalAccumulator {
        private int intervaloMinimo = Integer.MAX_VALUE;
        private int intervaloMaximo = Integer.MIN_VALUE;
        private final List<ProducerAwardInterval> minimos = new ArrayList<>();
        private final List<ProducerAwardInterval> maximos = new ArrayList<>();

        private void registrar(ProducerAwardInterval intervalo) {
            atualizarMinimos(intervalo);
            atualizarMaximos(intervalo);
        }

        private void atualizarMinimos(ProducerAwardInterval intervalo) {
            if (intervalo.intervalo() < intervaloMinimo) {
                intervaloMinimo = intervalo.intervalo();
                minimos.clear();
                minimos.add(intervalo);
                return;
            }

            if (intervalo.intervalo() == intervaloMinimo) {
                minimos.add(intervalo);
            }
        }

        private void atualizarMaximos(ProducerAwardInterval intervalo) {
            if (intervalo.intervalo() > intervaloMaximo) {
                intervaloMaximo = intervalo.intervalo();
                maximos.clear();
                maximos.add(intervalo);
                return;
            }

            if (intervalo.intervalo() == intervaloMaximo) {
                maximos.add(intervalo);
            }
        }

        private boolean possuiIntervalos() {
            return !minimos.isEmpty();
        }

        private ProducerAwardIntervalResult paraResultado() {
            return new ProducerAwardIntervalResult(
                    ordenar(minimos),
                    ordenar(maximos)
            );
        }

        private List<ProducerAwardInterval> ordenar(List<ProducerAwardInterval> intervalos) {
            return intervalos.stream()
                    .sorted(ORDENACAO_INTERVALO)
                    .toList();
        }
    }
}
