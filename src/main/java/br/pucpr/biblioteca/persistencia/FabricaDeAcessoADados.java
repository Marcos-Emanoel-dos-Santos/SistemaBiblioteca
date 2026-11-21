package br.pucpr.biblioteca.persistencia;

/**
 * <b>R08 — Abstract Factory: papel AbstractFactory.</b>
 * <p>
 * Cria, em conjunto, a unidade de trabalho e os repositórios de Livro, Movimentacao e
 * Relacionamento de UMA família (memória ou relacional). Cada repositório recebe a unidade de
 * trabalho que vai compartilhar; uma fábrica concreta recusa unidades de outra família
 * ({@link FamiliaIncompativelException}).
 * <p>
 * Não existe fábrica para o Oracle: ele é apenas réplica do MySQL e não recebe escritas
 * da aplicação.
 */
public interface FabricaDeAcessoADados {

    String nomeDaFamilia();

    UnidadeDeTrabalho criarUnidadeDeTrabalho();

    RepositorioDeLivros criarRepositorioDeLivros(UnidadeDeTrabalho unidadeDeTrabalho);

    RepositorioDeMovimentacao criarRepositorioDeMovimentacao(UnidadeDeTrabalho unidadeDeTrabalho);

    RepositorioDeRelacionamento criarRepositorioDeRelacionamento(UnidadeDeTrabalho unidadeDeTrabalho);
}
