package br.pucpr.biblioteca.organizacao;

import java.util.List;

/**
 * <b>R10 — Composite: papel Component.</b>
 * <p>
 * Permite tratar grupos ({@link GrupoDoAcervo}) e itens individuais ({@link ItemDeLivro})
 * de forma uniforme nas operações de contagem e listagem de identificadores de livros.
 */
public interface ComponenteDoAcervo {

    String getNome();

    /** Conta recursivamente todos os exemplares contidos neste nó (1 para folha; soma dos filhos para grupo). */
    int contarLivros();

    /** Lista os identificadores de livros contidos neste nó (sem expor objetos de domínio nem transações). */
    List<Integer> listarIdsLivros();
}
