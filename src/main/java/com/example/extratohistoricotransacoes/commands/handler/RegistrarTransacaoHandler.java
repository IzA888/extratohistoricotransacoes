package com.example.extratohistoricotransacoes.commands.handler;

import java.time.ZoneOffset;
import java.util.UUID;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.extratohistoricotransacoes.commands.model.Transacao;
import com.example.extratohistoricotransacoes.commands.repository.TransacaoRepository;
import com.example.extratohistoricotransacoes.commands.utils.TransacaoUtils;
import com.example.extratohistoricotransacoes.shared.TransacaoConcluidaEvent;

import jakarta.transaction.Transactional;

@Service
public class RegistrarTransacaoHandler {

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private ApplicationEventPublisher event;

    // Operações CRUD
    @Transactional
    public Transacao registrar(Transacao transacao) {
        TransacaoUtils.validar(transacao);
        BigDecimal taxaAplicada = transacao.getValor().add(aplicarTaxas(transacao));
        transacao.setValor(taxaAplicada);
        if (verificarDuplicacao(transacao)) {
            Transacao transacaoSalva = repository.save(transacao);
            event.publishEvent(new TransacaoConcluidaEvent(
                    transacaoSalva.getId(),
                    transacaoSalva.getConta(),
                    transacaoSalva.getDescricao(),
                    transacaoSalva.getValor().doubleValue(),
                    transacaoSalva.getTipo().toString(),
                    transacaoSalva.getDataHora().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()));
            return transacaoSalva;
        } else {
            throw new IllegalArgumentException("Transacao duplicada: " + transacao);
        }
    }

    @Transactional
    public Transacao atualizar(UUID id, Transacao transacao) {
        Transacao transacaoExistente = obterPorId(id);
        BeanUtils.copyProperties(transacao, transacaoExistente, "id");
        return repository.save(transacaoExistente);
    }

    public Transacao obterPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada para o id: " + id));
    }

    @Transactional
    public void deletarPorId(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Transação não encontrada para o id: " + id);
        }
    }

    // Processamento
    private Boolean verificarDuplicacao(Transacao transacao) {
        if (transacao.getId() == null) {
            return Boolean.TRUE;
        }

        Transacao existente = repository.findById(transacao.getId()).orElse(null);
        if (existente == null) {
            return Boolean.TRUE;
        }

        return !transacao.equals(existente);
    }

    private BigDecimal aplicarTaxas(Transacao transacao) {
        return switch (transacao.getTipo()) {
            case PIX_RECEBIDO, TED_RECEBIDA, COMPRA_DEBITO, ESTORNO -> BigDecimal.ZERO;
            case PIX_ENVIADO ->
                transacao.getValor().add(calcularPorcentagem(transacao.getValor(), BigDecimal.valueOf(0.5)));
            case TED_ENVIADA -> transacao.getValor().add(BigDecimal.valueOf(8.5));
            case COMPRA_CREDITO ->
                transacao.getValor().add(calcularPorcentagem(transacao.getValor(), BigDecimal.valueOf(2.5)));
        };
    }

    private BigDecimal calcularPorcentagem(BigDecimal valor, BigDecimal porcentagem) {
        if (valor == null || porcentagem == null) {
            return BigDecimal.ZERO;
        }

        return valor.multiply(porcentagem).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

}
