package br.pucpr.biblioteca.dominio.estado;

/**
 * <b>R01 — State: ConcreteState RESERVADO.</b>
 * Há uma reserva ativa registrada para um estudante. Aceita T3 ({@code emprestarLivro}) e
 * {@code removerReserva}.
 */
public final class EstadoReservado extends EstadoLivro {

    @Override
    public String nome() {
        return "RESERVADO";
    }

    @Override
    public EstadoLivro emprestarLivro() {
        return EstadosDoLivro.EMPRESTADO;
    }

    @Override
    public EstadoLivro removerReserva() {
        return EstadosDoLivro.LIVRE;
    }
}
