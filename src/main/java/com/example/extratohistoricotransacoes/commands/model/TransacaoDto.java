package com.example.extratohistoricotransacoes.commands.model;

public record TransacaoDto(
        Integer conta,
        String descricao,
        TipoTransacaoEnum tipo,
        double valor
) {

}
