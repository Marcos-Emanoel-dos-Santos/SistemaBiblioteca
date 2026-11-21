package br.pucpr.biblioteca.dto;

import br.pucpr.biblioteca.dominio.Livro;

/** Dados de um livro apresentados à interface (nunca o objeto de domínio). */
public record DadosLivro(int idLivro, String titulo, String estado) {

    public static DadosLivro de(Livro livro) {
        return new DadosLivro(livro.getIdLivro(), livro.getTitulo(), livro.getEstado().nome());
    }
}
