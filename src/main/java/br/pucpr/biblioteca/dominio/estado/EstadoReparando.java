package br.pucpr.biblioteca.dominio.estado;

/**
 * <b>R01 — State: ConcreteState REPARANDO.</b>
 * O livro está no setor de reparos. Aceita T10 ({@code retornarDoReparo} -> LIVRE),
 * {@code registrarLaudoIrrecuperavel} (-> INCINERADO) e {@code avaliarReparo(consertado)} do UML
 * (que delega a um dos dois eventos).
 */
public final class EstadoReparando extends EstadoLivro {

    @Override
    public String nome() {
        return "REPARANDO";
    }

    @Override
    public EstadoLivro retornarDoReparo() {
        return EstadosDoLivro.LIVRE;
    }

    @Override
    public EstadoLivro registrarLaudoIrrecuperavel() {
        return EstadosDoLivro.INCINERADO;
    }

    @Override
    public EstadoLivro avaliarReparo(boolean consertado) {
        return consertado ? retornarDoReparo() : registrarLaudoIrrecuperavel();
    }
}
