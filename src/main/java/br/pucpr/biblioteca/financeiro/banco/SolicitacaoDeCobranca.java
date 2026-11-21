package br.pucpr.biblioteca.financeiro.banco;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Contrato institucional de entrada para emissão de cobrança (R03 — Adapter). */
public record SolicitacaoDeCobranca(String idReferencia, String cpf, BigDecimal valor, LocalDate vencimento) {
}
