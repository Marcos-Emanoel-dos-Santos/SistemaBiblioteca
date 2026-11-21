package br.pucpr.biblioteca.nucleo;

import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.persistencia.RepositorioDeLivros;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Ponto interno de acesso <b>Acervo</b> (no UML da equipe: {@code AcervoDeLivros}).
 * Delega o armazenamento físico ao {@link RepositorioDeLivros} criado pela fábrica ativa (R08).
 */
public class AcervoDeLivros {

    private final RepositorioDeLivros repositorio;

    public AcervoDeLivros(RepositorioDeLivros repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "Repositório de livros é obrigatório.");
    }

    public Optional<Livro> consultarLivro(int idLivro) {
        return repositorio.buscar(idLivro);
    }

    public void inserirLivro(Livro livro) {
        repositorio.salvar(livro);
    }

    public void atualizarLivro(Livro livro) {
        repositorio.salvar(livro);
    }

    public void excluirLivro(int idLivro) {
        repositorio.remover(idLivro);
    }

    public List<Livro> listarLivros() {
        return repositorio.listar();
    }
}
