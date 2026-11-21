package br.pucpr.biblioteca.nucleo;

import br.pucpr.biblioteca.dominio.transacao.Devolucao;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;
import br.pucpr.biblioteca.dominio.transacao.Reserva;
import br.pucpr.biblioteca.persistencia.RepositorioDeMovimentacao;

import java.util.Objects;
import java.util.Optional;

/**
 * Ponto interno de acesso <b>Movimentacao</b> (Trabalho 02).
 * Mantém as associações distintas de {@link Reserva}, {@link Emprestimo} e {@link Devolucao},
 * delegando a persistência ao {@link RepositorioDeMovimentacao} da família ativa (R08).
 */
public class Movimentacao {

    private final RepositorioDeMovimentacao repositorio;

    public Movimentacao(RepositorioDeMovimentacao repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "Repositório de movimentação é obrigatório.");
    }

    // --- Associação Reserva ---

    public void reservarLivro(Reserva reserva) {
        repositorio.salvarReserva(reserva);
    }

    public Optional<Reserva> consultarReserva(int idLivro, int matricula) {
        return repositorio.buscarReserva(idLivro, matricula);
    }

    public void removerReserva(String idReserva) {
        repositorio.removerReserva(idReserva);
    }

    // --- Associação Empréstimo ---

    public void criarEmprestimo(Emprestimo emprestimo) {
        repositorio.salvarEmprestimo(emprestimo);
    }

    public Optional<Emprestimo> consultarEmprestimo(int idLivro, int matricula) {
        return repositorio.buscarEmprestimo(idLivro, matricula);
    }

    public void renovar(Emprestimo emprestimo) {
        repositorio.salvarEmprestimo(emprestimo);
    }

    public void removerEmprestimo(String idEmprestimo) {
        repositorio.removerEmprestimo(idEmprestimo);
    }

    // --- Associação Devolução ---

    public void criarDevolucao(Devolucao devolucao) {
        repositorio.salvarDevolucao(devolucao);
    }

    public Optional<Devolucao> consultarDevolucao(int idLivro, int matricula) {
        return repositorio.buscarDevolucao(idLivro, matricula);
    }
}
