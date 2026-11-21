package br.pucpr.biblioteca.dominio.estado;

/**
 * <b>R01 — State: ConcreteState LIVRE.</b> Livro cadastrado e disponível para reserva.
 * É o estado inicial (T1: inserirLivro → LIVRE).
 */
public class EstadoLivre extends EstadoLivro {

    @Override
    public String nome() {
        return "LIVRE";
    }
}
