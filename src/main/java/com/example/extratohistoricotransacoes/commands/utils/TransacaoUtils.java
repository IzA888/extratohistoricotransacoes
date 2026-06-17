package com.example.extratohistoricotransacoes.commands.utils;
import java.math.BigDecimal;

import com.example.extratohistoricotransacoes.commands.model.Transacao;

public class TransacaoUtils {

    private static final Integer CONTA_MINIMA = 10000;
    private static final Integer CONTA_MAXIMA = 9999999;
    private static final BigDecimal VALOR_MINIMO = new BigDecimal("0.01");
    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("9999999.99");

    public static void validar(Transacao transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transação não pode ser nula");
        }
        validarConta(transacao.getConta());
        validarValor(transacao.getValor());
    }

    private static void validarConta(Integer conta) {
        if (conta == null) {
            throw new IllegalArgumentException("Conta não pode ser nula");
        }
        if (conta < CONTA_MINIMA || conta > CONTA_MAXIMA) {
            throw new IllegalArgumentException("Conta deve estar entre " + CONTA_MINIMA + " e " + CONTA_MAXIMA);
        }
    }

    private static void validarValor(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        if (valor.compareTo(VALOR_MINIMO) < 0) {
            throw new IllegalArgumentException("Valor mínimo permitido é " + VALOR_MINIMO);
        }
        if (valor.compareTo(VALOR_MAXIMO) > 0) {
            throw new IllegalArgumentException("Valor máximo permitido é " + VALOR_MAXIMO);
        }
    }

}