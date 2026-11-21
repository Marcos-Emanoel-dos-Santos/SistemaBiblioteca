package br.pucpr.biblioteca.dominio.transacao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Devolução registrada (regra 9.2). {@code data} é a data da devolução e
 * {@code dataDeVencimento} é o vencimento do empréstimo encerrado — por isso a devolução
 * guarda sozinha os dados para calcular o atraso, mesmo após o empréstimo ser removido.
 */
public class Devolucao extends Transacao {

    public Devolucao(String id, int idLivro, int matricula, LocalDate dataDaDevolucao,
                     LocalDate vencimentoDoEmprestimo) {
        super(id, idLivro, matricula, dataDaDevolucao, vencimentoDoEmprestimo);
    }

    /** Dias corridos inteiros após o vencimento (zero se devolvido no prazo). */
    public long getDiasDeAtraso() {
        return Math.max(0, ChronoUnit.DAYS.between(getDataDeVencimento(), getData()));
    }
}
