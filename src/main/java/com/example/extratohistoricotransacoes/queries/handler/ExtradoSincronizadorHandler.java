package com.example.extratohistoricotransacoes.queries.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.extratohistoricotransacoes.queries.dto.ExtratoDto;
import com.example.extratohistoricotransacoes.shared.TransacaoConcluidaEvent;

@Component
public class ExtradoSincronizadorHandler {

    @Autowired
    private RedisTemplate<String, ExtratoDto> redisTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sincronizarRedis(TransacaoConcluidaEvent transacaoEvent) {
        String key = "conta:" + transacaoEvent.conta() + ":extrato";

        ExtratoDto extrato = new ExtratoDto(
            transacaoEvent.id().toString(),
            transacaoEvent.tipo(),
            transacaoEvent.valor(),
            transacaoEvent.descricao(),
            transacaoEvent.timestamp()
        );

        redisTemplate.opsForZSet().add(key, extrato, transacaoEvent.timestamp());
    }

}
