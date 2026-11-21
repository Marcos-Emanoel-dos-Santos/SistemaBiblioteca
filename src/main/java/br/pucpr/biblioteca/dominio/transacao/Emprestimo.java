package br.pucpr.biblioteca.dominio.transacao;

import java.time.LocalDate;

/**
 * Empréstimo ativo de um livro (regra 7.2). Guarda o número de renovações (regra 8).
 * <p>
 * A guarda {@code numRenovacoes < 2} NÃO fica aqui: ela pertence ao estado do Livro (R01),
 * que a avalia antes de chamar {@link #renovar(int)}.
 */
public class Emprestimo extends Transacao {

    private int numRenovacoes;

    public Emprestimo(String id, int idLivro, int matricula, LocalDate data,
                      LocalDate dataDeVencimento, int numRenovacoes) {
        super(id, idLivro, matricula, data, dataDeVencimento);
        this.numRenovacoes = numRenovacoes;
    }

    public int getNumRenovacoes() {
        return numRenovacoes;
    }

    /** Ação da renovação: acrescenta {@code dias} ao vencimento ATUAL e conta mais uma renovação. */
    public void renovar(int dias) {
        alterarDataDeVencimento(getDataDeVencimento().plusDays(dias));
        numRenovacoes++;
    }
}
