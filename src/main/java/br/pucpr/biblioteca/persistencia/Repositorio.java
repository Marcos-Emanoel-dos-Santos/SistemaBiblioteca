package br.pucpr.biblioteca.persistencia;

/** Contrato comum dos repositórios: todo repositório pertence a uma unidade de trabalho. */
public interface Repositorio {

    /** Unidade de trabalho (e, portanto, transação) da qual este repositório participa. */
    UnidadeDeTrabalho unidadeDeTrabalho();
}
