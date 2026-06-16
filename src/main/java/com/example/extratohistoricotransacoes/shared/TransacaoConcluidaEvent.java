package com.example.extratohistoricotransacoes.shared;

import java.util.UUID;

public record TransacaoConcluidaEvent(
    UUID id,
    Integer conta,
    String tipo,
    Double valor,
    String descricao,
    Long timestamp
) {

}
