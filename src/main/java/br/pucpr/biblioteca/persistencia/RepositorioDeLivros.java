package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Livro;

import java.util.List;
import java.util.Optional;

/** <b>R08 — Abstract Factory: produto abstrato "repositório de Livro".</b> */
public interface RepositorioDeLivros extends Repositorio {

    /** Insere ou atualiza o livro, incluindo o nome do estado atual. */
    void salvar(Livro livro);

    Optional<Livro> buscar(int idLivro);

    void remover(int idLivro);

    List<Livro> listar();
}
