package com.example.extratohistoricotransacoes.queries.handler;

import java.time.LocalDate;
import java.time.ZoneId;
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
        String key = "conta:" + contaId + ":extrato";
        long[] diasRange = normalizarData(periodo);
        long inicioDia = diasRange[0];
        long fimDia = diasRange[1];
        return redis.opsForZSet().reverseRangeByScore(key, inicioDia, fimDia);
    }

    public Set<ExtratoDto> obterExtratoSemana(String contaId) {
        String key = "conta:" + contaId + ":extrato";
        long[] range = obterExtratoSemanaRange(Hoje);
        return redis.opsForZSet().reverseRangeByScore(key, range[0], range[1]);
    }

    public Set<ExtratoDto> obterExtratoMes(String contaId) {
        String key = "conta:" + contaId + ":extrato";
        long[] range = obterExtratoMesRange(Hoje);
        return redis.opsForZSet().reverseRangeByScore(key, range[0], range[1]);
    }

    public Set<ExtratoDto> obterExtratoAno(String contaId) {
        String key = "conta:" + contaId + ":extrato";
        long[] range = obterExtratoAnoRange(Hoje);
        return redis.opsForZSet().reverseRangeByScore(key, range[0], range[1]);
    }

    public Set<ExtratoDto> obterExtratoXDias(String contaId, int dias) {
        if (dias <= 0) throw new IllegalArgumentException("dias must be > 0");
        LocalDate inicio = Hoje.minusDays(dias - 1);
        long inicioMs = inicio.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long fimMs = Hoje.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        String key = "conta:" + contaId + ":extrato";
        return redis.opsForZSet().reverseRangeByScore(key, inicioMs, fimMs);
    }

    public Set<ExtratoDto> obterExtratoXMeses(String contaId, int meses) {
        if (meses <= 0) throw new IllegalArgumentException("meses must be > 0");
        LocalDate inicio = Hoje.minusMonths(meses - 1).withDayOfMonth(1);
        long inicioMs = inicio.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long fimMs = Hoje.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        String key = "conta:" + contaId + ":extrato";
        return redis.opsForZSet().reverseRangeByScore(key, inicioMs, fimMs);
    }

    public Set<ExtratoDto> obterExtratoXAnos(String contaId, int anos) {
        if (anos <= 0) throw new IllegalArgumentException("anos must be > 0");
        LocalDate inicio = Hoje.minusYears(anos - 1).withDayOfYear(1);
        long inicioMs = inicio.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long fimMs = Hoje.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        String key = "conta:" + contaId + ":extrato";
        return redis.opsForZSet().reverseRangeByScore(key, inicioMs, fimMs);
    }

    //Helpers
    private long[] normalizarData(LocalDate[] periodo) {
        long inicioDia = periodo[0].atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long fimDia = periodo[1].atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new long[] { inicioDia, fimDia };
    }

    private long[] obterExtratoSemanaRange(LocalDate data) {
        LocalDate inicio = data.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate fim = data.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        long inicioMs = inicio.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long fimMs = fim.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new long[] { inicioMs, fimMs };
    }

    private long[] obterExtratoMesRange(LocalDate data) {
        LocalDate inicio = data.withDayOfMonth(1);
        LocalDate fim = data.with(TemporalAdjusters.lastDayOfMonth());
        long inicioMs = inicio.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long fimMs = fim.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new long[] { inicioMs, fimMs };
    }

    private long[] obterExtratoAnoRange(LocalDate data) {
        LocalDate inicio = data.withDayOfYear(1);
        LocalDate fim = data.with(TemporalAdjusters.lastDayOfYear());
        long inicioMs = inicio.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long fimMs = fim.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new long[] { inicioMs, fimMs };
    }
}
