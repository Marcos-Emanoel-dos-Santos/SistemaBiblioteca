package br.pucpr.biblioteca.dominio.estado;

import br.pucpr.biblioteca.dominio.excecao.TransicaoInvalidaException;

/**
 * <b>R01 — State: papel State.</b>
 * <p>
 * Cada estado do ciclo de vida do {@code Livro} é uma subclasse. Cada evento da máquina de
 * estados vira um método; o comportamento padrão de um evento é {@link #recusar(String)}
 * (lança exceção sem alterar o livro). Cada estado concreto sobrescreve apenas os eventos
 * que aceita, e cada método devolve o próximo estado.
 * <p>
 * Os estados não têm variáveis de instância, então uma única instância de cada um é
 * compartilhada por todos os livros (ver {@link EstadosDoLivro}).
 */
public abstract class EstadoLivro {

    /** Nome do estado, igual ao do diagrama de estados; é o valor gravado na persistência. */
    public abstract String nome();

    /** Comportamento padrão de qualquer evento não aceito pelo estado atual. */
    protected final EstadoLivro recusar(String evento) {
        throw new TransicaoInvalidaException(evento, nome());
    }

    @Override
    public String toString() {
        return nome();
    }
}
