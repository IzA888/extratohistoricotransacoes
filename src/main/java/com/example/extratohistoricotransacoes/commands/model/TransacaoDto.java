package com.example.extratohistoricotransacoes.commands.model;

public record TransacaoDto(
        String conta,
        String descricao,
        String tipo,
        double valor
) {

}
