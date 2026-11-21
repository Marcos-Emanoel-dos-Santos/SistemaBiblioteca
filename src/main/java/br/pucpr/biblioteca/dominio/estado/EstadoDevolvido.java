package br.pucpr.biblioteca.dominio.estado;

/**
 * <b>R01 — State: ConcreteState DEVOLVIDO.</b>
 * O livro foi devolvido ao atendente e aguarda inspeção. Enquanto estiver neste estado, não pode
 * ser reservado nem emprestado; aceita apenas {@code inspecionarLivro} (T7 -> LIVRE ou T8 -> REPARANDO).
 */
public final class EstadoDevolvido extends EstadoLivro {

    @Override
    public String nome() {
        return "DEVOLVIDO";
    }

    @Override
    public EstadoLivro inspecionarLivro(boolean aprovado) {
        return aprovado ? EstadosDoLivro.LIVRE : EstadosDoLivro.REPARANDO;
    }
}
