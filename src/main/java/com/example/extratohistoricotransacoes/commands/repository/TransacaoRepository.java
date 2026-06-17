// validações pesadas e insere no banco
package com.example.extratohistoricotransacoes.commands.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.extratohistoricotransacoes.commands.model.TipoTransacaoEnum;
import com.example.extratohistoricotransacoes.commands.model.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    // // Buscar por conta
    // List<Transacao> findByConta(Integer conta);
    
    // // Buscar por tipo
    // List<Transacao> findByTipo(TipoTransacaoEnum tipo);
    
    // // Buscar por período
    // List<Transacao> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    
    // // Buscar por conta e tipo
    // List<Transacao> findByContaAndTipo(Integer conta, TipoTransacaoEnum tipo);
    
    // // Buscar por valor
    // List<Transacao> findByValorGreaterThanEqual(BigDecimal valor);
    
    // // Buscar duplicadas
    // Optional<Transacao> findByContaAndTipoAndValorAndDataHora(Integer conta, TipoTransacaoEnum tipo, BigDecimal valor, LocalDateTime dataHora);

    void deleteById(UUID id);

    Optional<Transacao> findById(UUID id);

    Boolean existsById(UUID id);
}