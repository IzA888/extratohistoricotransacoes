package com.example.extratohistoricotransacoes.queries.dto;

public record ExtratoDto(
    String transacaoId,
    String tipo,
    Double valor,
    String descricao,
    Long timestamp
) {

}
