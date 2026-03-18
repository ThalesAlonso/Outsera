package com.outsera.razzies.loader;

import com.outsera.razzies.config.CsvProperties;
import com.outsera.razzies.entity.MovieEntity;
import com.outsera.razzies.entity.ProducerEntity;
import com.outsera.razzies.exception.CsvLoadingException;
import com.outsera.razzies.repository.MovieRepository;
import com.outsera.razzies.repository.ProducerRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Component
public class CsvMovieLoader implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvMovieLoader.class);
    private static final Set<String> CABECALHOS_OBRIGATORIOS = Set.of("year", "title", "studios", "producers", "winner");

    private final CsvProperties propriedadesCsv;
    private final MovieRepository repositorioFilme;
    private final ProducerRepository repositorioProdutor;
    private final ProducerNameParser interpretadorNomeProdutor;
    private final ResourceLoader carregadorRecursos;

    public CsvMovieLoader(
            CsvProperties propriedadesCsv,
            MovieRepository repositorioFilme,
            ProducerRepository repositorioProdutor,
            ProducerNameParser interpretadorNomeProdutor,
            ResourceLoader carregadorRecursos
    ) {
        this.propriedadesCsv = propriedadesCsv;
        this.repositorioFilme = repositorioFilme;
        this.repositorioProdutor = repositorioProdutor;
        this.interpretadorNomeProdutor = interpretadorNomeProdutor;
        this.carregadorRecursos = carregadorRecursos;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments argumentos) {
        if (repositorioFilme.count() > 0) {
            LOGGER.info("Ignorando bootstrap do CSV porque os filmes ja foram carregados");
            return;
        }

        Resource recurso = carregadorRecursos.getResource(propriedadesCsv.localizacao());
        if (!recurso.exists()) {
            throw new CsvLoadingException("Recurso CSV nao encontrado: " + propriedadesCsv.localizacao());
        }

        LOGGER.info("Iniciando bootstrap do CSV a partir de {}", propriedadesCsv.localizacao());

        int registrosValidos = 0;
        int vencedoresPersistidos = 0;

        try (Reader leitor = new InputStreamReader(recurso.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setDelimiter(';')
                     .setTrim(true)
                     .setIgnoreEmptyLines(true)
                     .build()
                     .parse(leitor)) {

            validarCabecalhos(parser);

            for (CSVRecord registro : parser) {
                LinhaCsvFilme linha = converterLinha(registro);
                if (linha == null) {
                    continue;
                }

                registrosValidos++;
                if (linha.vencedor()) {
                    vencedoresPersistidos++;
                }

                MovieEntity filme = new MovieEntity(
                        linha.anoPremiacao(),
                        linha.titulo(),
                        linha.estudios(),
                        linha.vencedor()
                );
                associarProdutores(filme, linha.produtores());
                repositorioFilme.save(filme);
            }
        } catch (IOException excecao) {
            throw new CsvLoadingException("Falha ao ler o recurso CSV: " + propriedadesCsv.localizacao(), excecao);
        }

        LOGGER.info("Bootstrap do CSV concluido: registrosValidos={}, vencedoresPersistidos={}", registrosValidos, vencedoresPersistidos);
    }

    private void validarCabecalhos(CSVParser parser) {
        Set<String> cabecalhosAtuais = parser.getHeaderMap().keySet();
        if (!cabecalhosAtuais.containsAll(CABECALHOS_OBRIGATORIOS)) {
            throw new CsvLoadingException(
                    "Cabecalho do CSV sem colunas obrigatorias. Esperado: " + CABECALHOS_OBRIGATORIOS
            );
        }
    }

    private LinhaCsvFilme converterLinha(CSVRecord registro) {
        Integer anoPremiacao = converterAno(registro.get("year"));
        String titulo = limparValor(registro.get("title"));
        String estudios = limparValor(registro.get("studios"));
        String produtoresBrutos = limparValor(registro.get("producers"));
        boolean vencedor = !limparValor(registro.get("winner")).isBlank();

        if (anoPremiacao == null || titulo.isBlank() || produtoresBrutos.isBlank()) {
            LOGGER.warn("Ignorando linha invalida do CSV {} por falta de dados obrigatorios", registro.getRecordNumber());
            return null;
        }

        List<ParsedProducer> produtores = interpretadorNomeProdutor.interpretar(produtoresBrutos);
        if (produtores.isEmpty()) {
            LOGGER.warn("Ignorando linha invalida do CSV {} porque nenhum produtor foi interpretado", registro.getRecordNumber());
            return null;
        }

        return new LinhaCsvFilme(anoPremiacao, titulo, estudios, vencedor, produtores);
    }

    private void associarProdutores(MovieEntity filme, List<ParsedProducer> produtoresProcessados) {
        for (ParsedProducer produtorProcessado : produtoresProcessados) {
            ProducerEntity produtor = repositorioProdutor.findByNomeNormalizado(produtorProcessado.nomeNormalizado())
                    .orElseGet(() -> repositorioProdutor.save(
                            new ProducerEntity(produtorProcessado.nome(), produtorProcessado.nomeNormalizado())
                    ));
            filme.adicionarProdutor(produtor);
        }
    }

    private Integer converterAno(String anoBruto) {
        String valor = limparValor(anoBruto);
        if (valor.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException excecao) {
            return null;
        }
    }

    private String limparValor(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private record LinhaCsvFilme(
            Integer anoPremiacao,
            String titulo,
            String estudios,
            boolean vencedor,
            List<ParsedProducer> produtores
    ) {
    }
}
