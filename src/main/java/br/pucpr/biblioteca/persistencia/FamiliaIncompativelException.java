package br.pucpr.biblioteca.persistencia;

/**
 * Lançada quando se tenta montar um repositório com uma unidade de trabalho de outra família
 * (ex.: repositório relacional com unidade de trabalho em memória). É o que impede misturar
 * famílias na mesma operação.
 */
public class FamiliaIncompativelException extends RuntimeException {

    public FamiliaIncompativelException(String familiaDaFabrica, UnidadeDeTrabalho recebida) {
        super("A fábrica da família '" + familiaDaFabrica + "' não aceita a unidade de trabalho "
                + recebida.getClass().getSimpleName() + ", que pertence a outra família.");
    }
}
