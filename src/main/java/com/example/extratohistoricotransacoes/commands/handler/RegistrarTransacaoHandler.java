package com.example.extratohistoricotransacoes.commands.handler;

import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.extratohistoricotransacoes.commands.model.Transacao;
import com.example.extratohistoricotransacoes.commands.repository.TransacaoRepository;
import com.example.extratohistoricotransacoes.shared.TransacaoConcluidaEvent;

import jakarta.transaction.Transactional;

@Service
public class RegistrarTransacaoHandler {

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private ApplicationEventPublisher event;

    @Transactional
    public Transacao registrar(Transacao transacao) {
        Transacao transacaoSalva = repository.save(transacao);
        event.publishEvent(new TransacaoConcluidaEvent(
            transacaoSalva.getId(),
            transacaoSalva.getConta(),
            transacaoSalva.getDescricao(),
            transacaoSalva.getValor().doubleValue(),
            transacaoSalva.getTipo().toString(),
            transacaoSalva.getDataHora().toEpochSecond(ZoneOffset.UTC)
        ));
        return transacaoSalva;
    }
    // Operações CRUD
    //public Transacao registrar(Transacao transacao);
    public void atualizar(UUID id, Transacao transacao);
    public void deletar(UUID id);
    public Optional<Transacao> obterPorId(UUID id);
    
    // Validação
    private void validarTransacao(Transacao transacao);
    
    // Processamento
    private void verificarDuplicacao(Transacao transacao);
    private BigDecimal aplicarTaxas(Transacao transacao);
    private void auditarRegistro(Transacao transacao);
    

}
