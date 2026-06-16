package com.example.extratohistoricotransacoes.commands.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.extratohistoricotransacoes.commands.model.Transacao;

@RestController
@RequestMapping("/commands")
public class CommandsRest {
    // POST - Registrar nova transação
    @PostMapping("/registrar")
    public ResponseEntity<Transacao> registrarTransacao(@RequestBody Transacao transacao);
    
    // POST - Validar antes de registrar
    @PostMapping("/validar")
    public ResponseEntity<Boolean> validarTransacao(@RequestBody Transacao transacao);
    
    // PUT - Atualizar transação
    @PutMapping("/{id}")
    public ResponseEntity<Transacao> atualizarTransacao(@PathVariable UUID id, @RequestBody Transacao transacao);
    
    // DELETE - Deletar transação
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable UUID id);
    
    // GET - Obter por ID
    @GetMapping("/{id}")
    public ResponseEntity<Transacao> obterPorId(@PathVariable UUID id);
}
