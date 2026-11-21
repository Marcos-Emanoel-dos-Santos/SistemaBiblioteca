package br.pucpr.biblioteca.financeiro.banco;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Notificação institucional de pagamento já traduzida pelo adaptador do banco (R03 — Adapter). */
public record NotificacaoDePagamento(String idCobranca, BigDecimal valorPago, LocalDate dataPagamento) {
}
