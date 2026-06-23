package com.example.extratohistoricotransacoes.queries.handler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.extratohistoricotransacoes.queries.dto.ExtratoDto;

@Component
public class ObterExtratoHandler {

    @Autowired
    private RedisTemplate<String, ExtratoDto> redis;

    private final LocalDate Hoje = LocalDate.now();

    public Set<ExtratoDto> obterExtratoTransacao(String contaId, LocalDate[] periodo) {
        String key = chaveContaExtrato(contaId);
        long[] diasRange = normalizarData(periodo);
        return redis.opsForZSet().reverseRangeByScore(key, diasRange[0], diasRange[1]);
    }

    public Set<ExtratoDto> obterExtratoSemana(String contaId) {
        String key = chaveContaExtrato(contaId);
        long[] range = obterExtratoSemanaRange(Hoje);
        return redis.opsForZSet().reverseRangeByScore(key, range[1], range[0]);
    }

    public Set<ExtratoDto> obterExtratoMes(String contaId) {
        String key = chaveContaExtrato(contaId);
        long[] range = obterExtratoMesRange(Hoje);
        return redis.opsForZSet().reverseRangeByScore(key, range[1], range[0]);
    }

    public Set<ExtratoDto> obterExtratoAno(String contaId) {
        String key = chaveContaExtrato(contaId);
        long[] range = obterExtratoAnoRange(Hoje);
        return redis.opsForZSet().reverseRangeByScore(key, range[1], range[0]);
    }

    public Set<ExtratoDto> obterExtratoXDias(String contaId, int dias) {
        if (dias <= 0)
            throw new IllegalArgumentException("dias must be > 0");
        LocalDate inicio = Hoje.minusDays(dias - 1);
        long[] data = normalizarData(inicio);
        String key = chaveContaExtrato(contaId);
        return redis.opsForZSet().reverseRangeByScore(key, data[1], data[0]);
    }

    public Set<ExtratoDto> obterExtratoXMeses(String contaId, int meses) {
        if (meses <= 0)
            throw new IllegalArgumentException("meses must be > 0");
        LocalDate inicio = Hoje.minusMonths(meses - 1).withDayOfMonth(1);
        long[] data = normalizarData(inicio);
        String key = chaveContaExtrato(contaId);
        return redis.opsForZSet().reverseRangeByScore(key, data[1], data[0]);
    }

    public Set<ExtratoDto> obterExtratoXAnos(String contaId, int anos) {
        if (anos <= 0)
            throw new IllegalArgumentException("anos must be > 0");
        LocalDate inicio = Hoje.minusYears(anos - 1).withDayOfYear(1);
        long[] data = normalizarData(inicio);
        String key = chaveContaExtrato(contaId);
        return redis.opsForZSet().reverseRangeByScore(key, data[1], data[0]);
    }

    private String chaveContaExtrato(String contaId) {
        return "conta:" + contaId + ":extrato";
    }

    // Helpers
    private long[] normalizarData(LocalDate[] periodo) {
        long inicioDia = periodo[0].atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        long fimDia = periodo[1].atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
        return new long[] { inicioDia, fimDia };
    }

    private long[] normalizarData(LocalDate inicio) {
        long inicioData = inicio.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        long fimData = Hoje.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
        return new long[] { inicioData, fimData };
    }

    private long[] obterExtratoSemanaRange(LocalDate data) {
        LocalDate inicio = data.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate fim = data.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        long inicioMs = inicio.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        long fimMs = fim.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
        return new long[] { inicioMs, fimMs };
    }

    private long[] obterExtratoMesRange(LocalDate data) {
        LocalDate inicio = data.withDayOfMonth(1);
        LocalDate fim = data.with(TemporalAdjusters.lastDayOfMonth());
        long inicioMs = inicio.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        long fimMs = fim.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
        return new long[] { inicioMs, fimMs };
    }

    private long[] obterExtratoAnoRange(LocalDate data) {
        LocalDate inicio = data.withDayOfYear(1);
        LocalDate fim = data.with(TemporalAdjusters.lastDayOfYear());
        long inicioMs = inicio.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        long fimMs = fim.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
        return new long[] { inicioMs, fimMs };
    }
}
