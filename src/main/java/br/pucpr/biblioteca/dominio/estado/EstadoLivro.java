package br.pucpr.biblioteca.dominio.estado;

import br.pucpr.biblioteca.dominio.excecao.TransicaoInvalidaException;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;

/**
 * <b>R01 — State: papel State.</b>
 * <p>
 * Cada estado do ciclo de vida do {@code Livro} é uma subclasse. Cada evento da máquina de
 * estados vira um método; o comportamento padrão de qualquer evento é {@link #recusar(String)}
 * (lança {@link TransicaoInvalidaException} sem alterar o livro nem transações).
 * Cada estado concreto sobrescreve apenas os eventos que aceita e devolve o próximo estado.
 */
public abstract class EstadoLivro {

    /** Nome do estado, igual ao do diagrama de estados; é o valor gravado na persistência. */
    public abstract String nome();

    /** T2: LIVRE -> RESERVADO. */
    public EstadoLivro reservarLivro() {
        return recusar("reservarLivro");
    }

    /** UML: RESERVADO -> LIVRE (cancelamento/remoção da reserva). */
    public EstadoLivro removerReserva() {
        return recusar("removerReserva");
    }

    /** T3: RESERVADO -> EMPRESTADO. */
    public EstadoLivro emprestarLivro() {
        return recusar("emprestarLivro");
    }

    /**
     * T4/T5: EMPRESTADO -> RENOVADO ou RENOVADO -> RENOVADO.
     * A guarda {@code numRenovacoes < 2} é avaliada no estado antes de alterar o vencimento.
     */
    public EstadoLivro renovarEmprestimo(Emprestimo emprestimo, int diasRenovacao) {
        return recusar("renovarEmprestimo");
    }

    /** T6: EMPRESTADO ou RENOVADO -> DEVOLVIDO. */
    public EstadoLivro registrarDevolucao() {
        return recusar("registrarDevolucao");
    }

    /** T7/T8: DEVOLVIDO -> LIVRE (aprovado) ou REPARANDO (danificado). */
    public EstadoLivro inspecionarLivro(boolean aprovado) {
        return recusar("inspecionarLivro");
    }

    /** T9: LIVRE -> REPARANDO (dano identificado no acervo). */
    public EstadoLivro enviarParaReparo() {
        return recusar("enviarParaReparo");
    }

    /** T10: REPARANDO -> LIVRE. */
    public EstadoLivro retornarDoReparo() {
        return recusar("retornarDoReparo");
    }

    /** R01 (Q2): REPARANDO -> INCINERADO (laudo irrecuperável). */
    public EstadoLivro registrarLaudoIrrecuperavel() {
        return recusar("registrarLaudoIrrecuperavel");
    }

    /**
     * Evento do UML da equipe em REPARANDO: quando {@code consertado} é verdadeiro, delega a
     * {@link #retornarDoReparo()}; caso contrário, delega a {@link #registrarLaudoIrrecuperavel()}.
     */
    public EstadoLivro avaliarReparo(boolean consertado) {
        return recusar("avaliarReparo");
    }

    /** T11: LIVRE -> (estado final / exclusão do acervo). */
    public void validarExclusao() {
        recusar("excluirLivro");
    }

    /** Comportamento padrão de qualquer evento não aceito pelo estado atual. */
    protected final EstadoLivro recusar(String evento) {
        throw new TransicaoInvalidaException(evento, nome());
    }

    @Override
    public String toString() {
        return nome();
    }
}
