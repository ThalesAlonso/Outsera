package com.outsera.razzies.loader;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class ProducerNameParser {

    private static final Pattern MULTIPLOS_ESPACOS = Pattern.compile("\\s+");
    private static final Pattern CONECTOR_PRODUTOR = Pattern.compile("(?i)\\s*,?\\s+and\\s+");

    public List<ParsedProducer> interpretar(String produtoresBrutos) {
        if (produtoresBrutos == null || produtoresBrutos.isBlank()) {
            return List.of();
        }

        String delimitadoresNormalizados = CONECTOR_PRODUTOR.matcher(produtoresBrutos).replaceAll(",");

        String[] candidatos = delimitadoresNormalizados.split(",");
        Map<String, ParsedProducer> produtoresUnicos = new LinkedHashMap<>();

        for (String candidato : candidatos) {
            String nomeLimpo = limparNome(candidato);
            if (nomeLimpo.isBlank()) {
                continue;
            }

            String nomeNormalizado = nomeLimpo.toLowerCase(Locale.ROOT);
            produtoresUnicos.putIfAbsent(nomeNormalizado, new ParsedProducer(nomeLimpo, nomeNormalizado));
        }

        return new ArrayList<>(produtoresUnicos.values());
    }

    private String limparNome(String valor) {
        return MULTIPLOS_ESPACOS.matcher(valor == null ? "" : valor.trim()).replaceAll(" ");
    }
}
