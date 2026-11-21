package br.pucpr.biblioteca.dominio.estado;

import br.pucpr.biblioteca.dominio.excecao.RegraDeNegocioException;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;

import java.util.Objects;

/**
 * <b>R01 — State: ConcreteState RENOVADO.</b>
 * O empréstimo já foi renovado 1 ou 2 vezes.
 * <p>
 * Na transição T5 ({@code renovarEmprestimo}), a guarda {@code numRenovacoes < 2} é avaliada
 * <b>antes</b> de alterar o vencimento do empréstimo: na 3ª tentativa, a exceção é lançada
 * preservando integralmente o vencimento e o estado atual.
 */
public final class EstadoRenovado extends EstadoLivro {

    @Override
    public String nome() {
        return "RENOVADO";
    }

    @Override
    public EstadoLivro renovarEmprestimo(Emprestimo emprestimo, int diasRenovacao) {
        Objects.requireNonNull(emprestimo, "Empréstimo é obrigatório para renovação.");
        if (emprestimo.getNumRenovacoes() >= 2) {
            throw new RegraDeNegocioException(
                    "Renovação recusada: limite máximo de 2 renovações atingido para o livro "
                            + emprestimo.getIdLivro() + ".");
        }
        emprestimo.renovar(diasRenovacao);
        return EstadosDoLivro.RENOVADO;
    }

    @Override
    public EstadoLivro registrarDevolucao() {
        return EstadosDoLivro.DEVOLVIDO;
    }
}
