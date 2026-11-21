package br.pucpr.biblioteca.dominio.excecao;

/**
 * Lançada quando um evento chega a um estado do {@code Livro} que não o aceita (R01 — State).
 * O livro permanece exatamente como estava.
 */
public class TransicaoInvalidaException extends RegraDeNegocioException {

    public TransicaoInvalidaException(String evento, String estadoAtual) {
        super("Operação '" + evento + "' não é permitida para livro no estado " + estadoAtual + ".");
    }
}
