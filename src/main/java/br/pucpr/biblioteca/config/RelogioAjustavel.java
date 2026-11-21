package br.pucpr.biblioteca.config;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Relógio de infraestrutura que permite avançar dias em testes e na demonstração guiada
 * sem alterar nenhuma regra de domínio.
 */
public final class RelogioAjustavel extends Clock {

    private Instant instanteAtual;
    private final ZoneId zona;

    public RelogioAjustavel(LocalDate dataInicial, ZoneId zona) {
        this.zona = zona;
        this.instanteAtual = dataInicial.atTime(10, 0).atZone(zona).toInstant();
    }

    public static RelogioAjustavel em(LocalDate dataInicial) {
        return new RelogioAjustavel(dataInicial, ZoneId.systemDefault());
    }

    public void avancarDias(long dias) {
        this.instanteAtual = this.instanteAtual.plus(dias, ChronoUnit.DAYS);
    }

    public void definirData(LocalDate novaData) {
        this.instanteAtual = novaData.atTime(10, 0).atZone(zona).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return zona;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new RelogioAjustavel(LocalDate.ofInstant(instanteAtual, zone), zone);
    }

    @Override
    public Instant instant() {
        return instanteAtual;
    }
}
