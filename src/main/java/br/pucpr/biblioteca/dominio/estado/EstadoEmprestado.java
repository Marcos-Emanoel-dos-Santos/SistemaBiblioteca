package br.pucpr.biblioteca.dominio.estado;

import br.pucpr.biblioteca.dominio.excecao.RegraDeNegocioException;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;

import java.util.Objects;

/**
 * <b>R01 — State: ConcreteState EMPRESTADO.</b>
 * O livro está com o estudante, sem renovações ainda. Aceita T4 ({@code renovarEmprestimo} com
 * guarda {@code numRenovacoes < 2}) e T6 ({@code registrarDevolucao}).
 */
public final class EstadoEmprestado extends EstadoLivro {

    @Override
    public String nome() {
        return "EMPRESTADO";
    }

    @Override
    public EstadoLivro renovarEmprestimo(Emprestimo emprestimo, int diasRenovacao) {
        Objects.requireNonNull(emprestimo, "Empréstimo é obrigatório para renovação.");
        if (emprestimo.getNumRenovacoes() >= 2) {
            throw new RegraDeNegocioException(
                    "Limite de duas renovações já atingido para o livro " + emprestimo.getIdLivro() + ".");
        }
        emprestimo.renovar(diasRenovacao);
        return EstadosDoLivro.RENOVADO;
    }

    @Override
    public EstadoLivro registrarDevolucao() {
        return EstadosDoLivro.DEVOLVIDO;
    }
}
