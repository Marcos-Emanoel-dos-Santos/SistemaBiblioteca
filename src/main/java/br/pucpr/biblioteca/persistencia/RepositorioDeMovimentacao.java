package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.transacao.Devolucao;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;
import br.pucpr.biblioteca.dominio.transacao.Reserva;

import java.util.Optional;

/**
 * <b>R08 — Abstract Factory: produto abstrato "repositório de Movimentacao".</b>
 * Mantém as três associações distintas da Movimentacao: reservas, empréstimos e devoluções.
 */
public interface RepositorioDeMovimentacao extends Repositorio {

    void salvarReserva(Reserva reserva);

    Optional<Reserva> buscarReserva(int idLivro, int matricula);

    void removerReserva(String idReserva);

    /** Insere ou atualiza (a renovação altera vencimento e número de renovações). */
    void salvarEmprestimo(Emprestimo emprestimo);

    Optional<Emprestimo> buscarEmprestimo(int idLivro, int matricula);

    void removerEmprestimo(String idEmprestimo);

    void salvarDevolucao(Devolucao devolucao);

    /** Devolução mais recente do livro por aquele estudante. */
    Optional<Devolucao> buscarDevolucao(int idLivro, int matricula);
}
