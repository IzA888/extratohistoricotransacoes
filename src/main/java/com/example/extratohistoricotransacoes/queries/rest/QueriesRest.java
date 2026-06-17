package com.example.extratohistoricotransacoes.queries.rest;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.extratohistoricotransacoes.queries.dto.ExtratoDto;
import com.example.extratohistoricotransacoes.queries.handler.ObterExtratoHandler;

@RestController
@RequestMapping("/queries")
public class QueriesRest {

    @Autowired
    private ObterExtratoHandler service;

    @GetMapping("/{contaId}/transacoes")
    public ResponseEntity<Set<ExtratoDto>> obterExtratoTransacao(
            @PathVariable String contaId,
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.obterExtratoTransacao(contaId, new LocalDate[] { inicio, fim }));
    }

    @GetMapping("/{contaId}/semana")
    public ResponseEntity<Set<ExtratoDto>> obterExtratoSemana(@PathVariable String contaId) {
        return ResponseEntity.ok(service.obterExtratoSemana(contaId));
    }

    @GetMapping("/{contaId}/mes")
    public ResponseEntity<Set<ExtratoDto>> obterExtratoMes(@PathVariable String contaId) {
        return ResponseEntity.ok(service.obterExtratoMes(contaId));
    }

    @GetMapping("/{contaId}/ano")
    public ResponseEntity<Set<ExtratoDto>> obterExtratoAno(@PathVariable String contaId) {
        return ResponseEntity.ok(service.obterExtratoAno(contaId));
    }

    @GetMapping("/{contaId}/ultimos-dias")
    public ResponseEntity<Set<ExtratoDto>> obterExtratoXDias(@PathVariable String contaId,
            @RequestParam("dias") int dias) {
        return ResponseEntity.ok(service.obterExtratoXDias(contaId, dias));
    }

    @GetMapping("/{contaId}/ultimos-meses")
    public ResponseEntity<Set<ExtratoDto>> obterExtratoXMeses(@PathVariable String contaId,
            @RequestParam("meses") int meses) {
        return ResponseEntity.ok(service.obterExtratoXMeses(contaId, meses));
    }

    @GetMapping("/{contaId}/ultimos-anos")
    public ResponseEntity<Set<ExtratoDto>> obterExtratoXAnos(@PathVariable String contaId,
            @RequestParam("anos") int anos) {
        return ResponseEntity.ok(service.obterExtratoXAnos(contaId, anos));
    }
}
