package com.outsera.razzies.mapper;

import com.outsera.razzies.dto.AwardIntervalResponse;
import com.outsera.razzies.dto.AwardIntervalItemResponse;
import com.outsera.razzies.service.ProducerAwardInterval;
import com.outsera.razzies.service.ProducerAwardIntervalResult;
import org.springframework.stereotype.Component;

@Component
public class AwardIntervalMapper {

    public AwardIntervalResponse paraResposta(ProducerAwardIntervalResult resultado) {
        return new AwardIntervalResponse(
                resultado.minimo().stream().map(this::paraItemResposta).toList(),
                resultado.maximo().stream().map(this::paraItemResposta).toList()
        );
    }

    private AwardIntervalItemResponse paraItemResposta(ProducerAwardInterval intervalo) {
        return new AwardIntervalItemResponse(
                intervalo.produtor(),
                intervalo.intervalo(),
                intervalo.vitoriaAnterior(),
                intervalo.vitoriaSeguinte()
        );
    }
}
