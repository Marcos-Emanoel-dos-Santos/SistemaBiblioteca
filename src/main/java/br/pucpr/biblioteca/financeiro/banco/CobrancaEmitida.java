package br.pucpr.biblioteca.financeiro.banco;

import java.math.BigDecimal;

/** Contrato institucional de saída de uma cobrança emitida (R03 — Adapter). */
public record CobrancaEmitida(String idCobranca, String banco, String codigoBarras,
                              BigDecimal valor, SituacaoCobranca situacao) {
}
