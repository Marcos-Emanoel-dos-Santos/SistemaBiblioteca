package br.pucpr.biblioteca.dominio;

import br.pucpr.biblioteca.dominio.estado.EstadoLivro;
import br.pucpr.biblioteca.dominio.estado.EstadosDoLivro;

/**
 * Livro do acervo (UML: {@code Livro(idLivro, titulo, estado)}).
 * <p>
 * <b>R01 — State: papel Context.</b> O atributo {@code estado} é um objeto {@link EstadoLivro}
 * (e não uma {@code String}); os eventos do ciclo de vida são delegados a ele.
 */
public class Livro {

    private final int idLivro;
    private String titulo;
    private EstadoLivro estado;

    /** T1 — inserirLivro: todo livro novo nasce LIVRE. */
    public Livro(int idLivro, String titulo) {
        this(idLivro, titulo, EstadosDoLivro.LIVRE);
    }

    private Livro(int idLivro, String titulo, EstadoLivro estado) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.estado = estado;
    }

    /** Reconstitui um livro lido da persistência, com o estado que estava gravado. */
    public static Livro reconstituir(int idLivro, String titulo, String nomeDoEstado) {
        return new Livro(idLivro, titulo, EstadosDoLivro.porNome(nomeDoEstado));
    }

    /** Regra 1.2: atualiza os dados do livro (o id é a identidade e não muda). */
    public void atualizarLivro(String titulo) {
        this.titulo = titulo;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public String getTitulo() {
        return titulo;
    }

    public EstadoLivro getEstado() {
        return estado;
    }
}
