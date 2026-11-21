package br.pucpr.biblioteca.circulacao.devolucao;

/**
 * <b>R05 — Observer: papel Observer.</b>
 * Novos interessados na devolução implementam esta interface e são registrados no
 * {@link PublicadorDeDevolucao} sem alterar {@code TratadorDeDevolucao}.
 */
public interface OuvinteDeDevolucao {

    void aoDevolver(EventoDevolucao evento);
}
