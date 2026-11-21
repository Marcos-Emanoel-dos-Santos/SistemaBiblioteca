package br.pucpr.biblioteca.persistencia;

import java.util.function.Supplier;

/**
 * <b>R08 — Abstract Factory: produto "unidade de trabalho".</b>
 * <p>
 * Delimita uma transação de negócio: tudo o que os repositórios da MESMA família gravarem
 * entre {@link #iniciar()} e {@link #confirmar()} é efetivado junto, ou desfeito junto em
 * {@link #desfazer()}. É usada pela rotina de cadastro (R07) e pela fachada de circulação (R04).
 */
public interface UnidadeDeTrabalho {

    void iniciar();

    void confirmar();

    void desfazer();

    boolean emAndamento();

    /**
     * Executa a operação como uma transação: confirma se terminar normalmente e desfaz se
     * lançar exceção. Se já houver uma transação em andamento, a operação participa dela.
     */
    default <T> T executar(Supplier<T> operacao) {
        if (emAndamento()) {
            return operacao.get();
        }
        iniciar();
        try {
            T resultado = operacao.get();
            confirmar();
            return resultado;
        } catch (RuntimeException falha) {
            desfazer();
            throw falha;
        }
    }
}
