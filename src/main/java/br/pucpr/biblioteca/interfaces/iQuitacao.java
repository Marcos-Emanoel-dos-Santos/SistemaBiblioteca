package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.financeiro.banco.NotificacaoDePagamento;

/**
 * Contrato fornecido pelo componente Financeiro para receber notificações de pagamento já
 * traduzidas pelos adaptadores bancários (Trabalho 02 / R03).
 */
public interface iQuitacao {

    /**
     * Registra a quitação da cobrança identificada. É idempotente: notificações repetidas
     * para uma multa já quitada não geram segunda quitação e retornam {@code false}.
     */
    boolean registrarQuitacao(NotificacaoDePagamento notificacao);
}
