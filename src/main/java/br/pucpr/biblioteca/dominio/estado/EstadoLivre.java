package br.pucpr.biblioteca.dominio.estado;

/**
 * <b>R01 — State: ConcreteState LIVRE.</b> Livro cadastrado e disponível para reserva.
 * Aceita T2 ({@code reservarLivro}), T9 ({@code enviarParaReparo}) e T11 ({@code excluirLivro}).
 * Não possui transição direta para INCINERADO (Q2).
 */
public final class EstadoLivre extends EstadoLivro {

    @Override
    public String nome() {
        return "LIVRE";
    }

    @Override
    public EstadoLivro reservarLivro() {
        return EstadosDoLivro.RESERVADO;
    }

    @Override
    public EstadoLivro enviarParaReparo() {
        return EstadosDoLivro.REPARANDO;
    }

    @Override
    public void validarExclusao() {
        // T11: exclusão permitida quando o livro está LIVRE.
    }
}
