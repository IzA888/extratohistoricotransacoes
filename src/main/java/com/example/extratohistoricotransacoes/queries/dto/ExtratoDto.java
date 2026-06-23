package com.example.extratohistoricotransacoes.queries.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtratoDto implements Serializable {

    private String transacaoId;
    private String tipo;
    private Double valor;
    private String descricao;
    private Long timestamp;

}
