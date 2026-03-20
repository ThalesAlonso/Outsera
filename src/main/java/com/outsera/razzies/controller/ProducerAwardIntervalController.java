package com.outsera.razzies.controller;

import com.outsera.razzies.dto.AwardIntervalResponse;
import com.outsera.razzies.mapper.AwardIntervalMapper;
import com.outsera.razzies.service.ProducerAwardIntervalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/producers")
@Tag(name = "Producers", description = "Consultas de premiacao dos produtores")
public class ProducerAwardIntervalController {

    private final ProducerAwardIntervalService servicoIntervaloPremioProdutor;
    private final AwardIntervalMapper mapeadorIntervaloPremio;

    public ProducerAwardIntervalController(
            ProducerAwardIntervalService servicoIntervaloPremioProdutor,
            AwardIntervalMapper mapeadorIntervaloPremio
    ) {
        this.servicoIntervaloPremioProdutor = servicoIntervaloPremioProdutor;
        this.mapeadorIntervaloPremio = mapeadorIntervaloPremio;
    }

    @GetMapping("/award-intervals")
    @Operation(
            summary = "Consulta intervalos de premios",
            description = "Retorna os produtores com o menor e o maior intervalo entre duas vitorias consecutivas."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Intervalos calculados com sucesso",
                    content = @Content(schema = @Schema(implementation = AwardIntervalResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Erro interno inesperado")
    })
    public ResponseEntity<AwardIntervalResponse> buscarIntervalosPremiacao() {
        AwardIntervalResponse resposta = mapeadorIntervaloPremio.paraResposta(
                servicoIntervaloPremioProdutor.buscarIntervalosPremiacao()
        );
        return ResponseEntity.ok(resposta);
    }
}
