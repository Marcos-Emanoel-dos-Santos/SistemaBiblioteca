package br.pucpr.biblioteca.persistencia.jdbc;

import br.pucpr.biblioteca.persistencia.FabricaDeAcessoADados;
import br.pucpr.biblioteca.persistencia.FamiliaIncompativelException;
import br.pucpr.biblioteca.persistencia.RepositorioDeLivros;
import br.pucpr.biblioteca.persistencia.RepositorioDeMovimentacao;
import br.pucpr.biblioteca.persistencia.RepositorioDeRelacionamento;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.Objects;

/**
 * <b>R08 — Abstract Factory: ConcreteFactory da família relacional (MySQL em produção).</b>
 * <p>
 * Todas as escritas seguem para o banco principal (MySQL via JDBC). O Oracle permanece apenas
 * como réplica externa e não possui fábrica nem escrita pela aplicação.
 */
public final class FabricaRelacionalMySQL implements FabricaDeAcessoADados {

    private final String url;
    private final String usuario;
    private final String senha;

    public FabricaRelacionalMySQL(String url, String usuario, String senha) {
        this.url = Objects.requireNonNull(url, "URL JDBC é obrigatória.");
        this.usuario = usuario;
        this.senha = senha;
    }

    public FabricaRelacionalMySQL(String url) {
        this(url, null, null);
    }

    @Override
    public String nomeDaFamilia() {
        return "mysql";
    }

    @Override
    public UnidadeDeTrabalho criarUnidadeDeTrabalho() {
        return new UnidadeDeTrabalhoJdbc(url, usuario, senha);
    }

    @Override
    public RepositorioDeLivros criarRepositorioDeLivros(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioDeLivrosJdbc(validarUnidade(unidadeDeTrabalho));
    }

    @Override
    public RepositorioDeMovimentacao criarRepositorioDeMovimentacao(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioDeMovimentacaoJdbc(validarUnidade(unidadeDeTrabalho));
    }

    @Override
    public RepositorioDeRelacionamento criarRepositorioDeRelacionamento(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioDeRelacionamentoJdbc(validarUnidade(unidadeDeTrabalho));
    }

    private UnidadeDeTrabalhoJdbc validarUnidade(UnidadeDeTrabalho unidadeDeTrabalho) {
        Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho é obrigatória.");
        if (unidadeDeTrabalho instanceof UnidadeDeTrabalhoJdbc jdbc) {
            return jdbc;
        }
        throw new FamiliaIncompativelException(nomeDaFamilia(), unidadeDeTrabalho);
    }
}
