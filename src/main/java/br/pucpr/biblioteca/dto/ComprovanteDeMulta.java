package br.pucpr.biblioteca.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Comprovante imutável de multa emitida/consultada (f6 — R02/R03). */
public record ComprovanteDeMulta(String idMulta, String idDevolucao, String idCobranca,
                                 String cpfEstudante, long diasDeAtraso, BigDecimal valor,
                                 String politicaAplicada, String situacao,
                                 String codigoBarras, LocalDate dataEmissao,
                                 LocalDate dataQuitacao) {
}
