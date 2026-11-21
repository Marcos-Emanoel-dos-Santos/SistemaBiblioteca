package br.pucpr.biblioteca.organizacao;

import br.pucpr.biblioteca.dominio.excecao.DadosInvalidosException;

import java.util.List;
import java.util.Objects;

/**
 * <b>R10 — Composite: papel Leaf.</b>
 * <p>
 * Referencia um livro do acervo exclusivamente pelo seu identificador ({@code idLivro}).
 * Não mantém referência à instância de {@code Livro} nem a {@code Movimentacao}, garantindo
 * que organizar ou mover o item na árvore nunca altere identidade, estado ou empréstimos.
 */
public final class ItemDeLivro implements ComponenteDoAcervo {

    private final int idLivro;

    public ItemDeLivro(int idLivro) {
        if (idLivro <= 0) {
            throw new DadosInvalidosException("Identificador do livro deve ser positivo.");
        }
        this.idLivro = idLivro;
    }

    public int getIdLivro() {
        return idLivro;
    }

    @Override
    public String getNome() {
        return "Livro #" + idLivro;
    }

    @Override
    public int contarLivros() {
        return 1;
    }

    @Override
    public List<Integer> listarIdsLivros() {
        return List.of(idLivro);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemDeLivro that)) {
            return false;
        }
        return idLivro == that.idLivro;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLivro);
    }

    @Override
    public String toString() {
        return getNome();
    }
}
