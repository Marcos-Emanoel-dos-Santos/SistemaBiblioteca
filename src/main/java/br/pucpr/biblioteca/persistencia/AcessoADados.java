package br.pucpr.biblioteca.persistencia;

/**
 * Conjunto coerente de objetos de acesso a dados, todos criados pela MESMA fábrica e
 * compartilhando a MESMA unidade de trabalho.
 */
public record AcessoADados(UnidadeDeTrabalho unidadeDeTrabalho,
                           RepositorioDeLivros livros,
                           RepositorioDeMovimentacao movimentacao,
                           RepositorioDeRelacionamento relacionamento) {

    /** Único ponto em que a família é materializada a partir de uma fábrica. */
    public static AcessoADados criarCom(FabricaDeAcessoADados fabrica) {
        UnidadeDeTrabalho unidade = fabrica.criarUnidadeDeTrabalho();
        return new AcessoADados(
                unidade,
                fabrica.criarRepositorioDeLivros(unidade),
                fabrica.criarRepositorioDeMovimentacao(unidade),
                fabrica.criarRepositorioDeRelacionamento(unidade));
    }
}
