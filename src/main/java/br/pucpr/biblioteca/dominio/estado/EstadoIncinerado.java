package br.pucpr.biblioteca.dominio.estado;

/**
 * <b>R01 — State: ConcreteState INCINERADO (estado final).</b>
 * O livro foi considerado irrecuperável e destruído. Todas as operações são recusadas pelo
 * comportamento padrão de {@link EstadoLivro}.
 */
public final class EstadoIncinerado extends EstadoLivro {

    @Override
    public String nome() {
        return "INCINERADO";
    }
}
