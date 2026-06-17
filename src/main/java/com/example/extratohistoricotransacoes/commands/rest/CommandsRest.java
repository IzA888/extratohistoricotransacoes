package com.example.extratohistoricotransacoes.commands.rest;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.extratohistoricotransacoes.commands.handler.RegistrarTransacaoHandler;
import com.example.extratohistoricotransacoes.commands.model.Transacao;
import com.example.extratohistoricotransacoes.commands.model.TransacaoDto;

@RestController
@RequestMapping("/commands")
public class CommandsRest {

    @Autowired
    private RegistrarTransacaoHandler service;

    @PostMapping("/registrar")
    public ResponseEntity<TransacaoDto> registrarTransacao(@RequestBody TransacaoDto transacaoDto) {
        Transacao transacao = toEntity(transacaoDto);
        Transacao saved = service.registrar(transacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoDto> atualizarTransacao(@PathVariable UUID id, @RequestBody Transacao transacao) {
        Transacao updated = service.atualizar(id, transacao);
        return ResponseEntity.ok().body(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable UUID id) {
        service.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    private Transacao toEntity(TransacaoDto transacaoDto) {
        Transacao transacao = new Transacao();
        BeanUtils.copyProperties(transacaoDto, transacao);
        return transacao;
    }

    private TransacaoDto toDto(Transacao transacao) {
        return new TransacaoDto(
            transacao.getConta().toString(),
            transacao.getDescricao(),
            transacao.getTipo().toString(),
            transacao.getValor().doubleValue()
        );
    }
}
