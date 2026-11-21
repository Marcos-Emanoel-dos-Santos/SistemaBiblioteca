package br.pucpr.biblioteca.persistencia.memoria;

/**
 * Contrato interno da família em memória para que todos os repositórios ligados à mesma
 * {@link UnidadeDeTrabalhoEmMemoria} capturem e restaurem seus dados em bloco no caso de rollback.
 */
interface ParticipanteTransacional {

    void capturarEstado();

    void confirmarEstado();

    void restaurarEstado();
}
