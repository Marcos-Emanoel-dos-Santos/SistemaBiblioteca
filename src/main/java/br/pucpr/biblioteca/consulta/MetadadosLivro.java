package br.pucpr.biblioteca.consulta;

import br.pucpr.biblioteca.dominio.Livro;

/**
 * Metadados públicos de um livro retornados pelo serviço de consulta (R09 — Decorator).
 * <p>
 * Propositalmente contém apenas {@code idLivro} e {@code titulo} — <b>sem o estado do livro</b>.
 * Assim, dados em cache nunca podem autorizar uma reserva; a reserva sempre consulta o estado
 * atual diretamente na fonte autoritativa ({@code AcervoDeLivros}).
 */
public record MetadadosLivro(int idLivro, String titulo) {

    public static MetadadosLivro de(Livro livro) {
        return new MetadadosLivro(livro.getIdLivro(), livro.getTitulo());
    }
}
