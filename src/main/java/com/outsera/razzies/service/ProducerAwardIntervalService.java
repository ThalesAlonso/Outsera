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
import java.util.IntSummaryStatistics;
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
        Map<String, List<Integer>> vitoriasPorProdutor = new HashMap<>();

        for (MovieEntity filme : filmesVencedores) {
            for (ProducerEntity produtor : filme.getProdutores()) {
                vitoriasPorProdutor.computeIfAbsent(produtor.getNome(), chave -> new ArrayList<>())
                        .add(filme.getAnoPremiacao());
            }
        }

        List<ProducerAwardInterval> intervalos = montarIntervalos(vitoriasPorProdutor);
        if (intervalos.isEmpty()) {
            LOGGER.info("Nenhum produtor possui vitorias suficientes para gerar intervalo");
            return new ProducerAwardIntervalResult(List.of(), List.of());
        }

        IntSummaryStatistics stats = intervalos.stream()
                .mapToInt(ProducerAwardInterval::intervalo)
                .summaryStatistics();
        int intervaloMinimo = stats.getMin();
        int intervaloMaximo = stats.getMax();

        List<ProducerAwardInterval> minimo = intervalos.stream()
                .filter(intervalo -> intervalo.intervalo() == intervaloMinimo)
                .sorted(ORDENACAO_INTERVALO)
                .toList();
        List<ProducerAwardInterval> maximo = intervalos.stream()
                .filter(intervalo -> intervalo.intervalo() == intervaloMaximo)
                .sorted(ORDENACAO_INTERVALO)
                .toList();

        LOGGER.info("Intervalos de premiacao calculados: intervaloMinimo={}, intervaloMaximo={}", intervaloMinimo, intervaloMaximo);
        return new ProducerAwardIntervalResult(minimo, maximo);
    }

    private List<ProducerAwardInterval> montarIntervalos(Map<String, List<Integer>> vitoriasPorProdutor) {
        List<ProducerAwardInterval> intervalos = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entrada : vitoriasPorProdutor.entrySet()) {
            List<Integer> vitorias = entrada.getValue().stream()
                    .sorted()
                    .toList();

            for (int indice = 1; indice < vitorias.size(); indice++) {
                int vitoriaAnterior = vitorias.get(indice - 1);
                int vitoriaSeguinte = vitorias.get(indice);
                intervalos.add(new ProducerAwardInterval(
                        entrada.getKey(),
                        vitoriaSeguinte - vitoriaAnterior,
                        vitoriaAnterior,
                        vitoriaSeguinte
                ));
            }
        }

        return intervalos;
    }
}
