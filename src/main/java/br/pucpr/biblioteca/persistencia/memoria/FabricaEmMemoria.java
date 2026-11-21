package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.persistencia.FabricaDeAcessoADados;
import br.pucpr.biblioteca.persistencia.FamiliaIncompativelException;
import br.pucpr.biblioteca.persistencia.RepositorioDeLivros;
import br.pucpr.biblioteca.persistencia.RepositorioDeMovimentacao;
import br.pucpr.biblioteca.persistencia.RepositorioDeRelacionamento;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.Objects;

/**
 * <b>R08 — Abstract Factory: ConcreteFactory da família em memória (perfil de testes).</b>
 * <p>
 * Garante que todos os repositórios criados pertençam exclusivamente à família em memória
 * e compartilhem uma {@link UnidadeDeTrabalhoEmMemoria}.
 */
public final class FabricaEmMemoria implements FabricaDeAcessoADados {

    @Override
    public String nomeDaFamilia() {
        return "memoria";
    }

    @Override
    public UnidadeDeTrabalho criarUnidadeDeTrabalho() {
        return new UnidadeDeTrabalhoEmMemoria();
    }

    @Override
    public RepositorioDeLivros criarRepositorioDeLivros(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioDeLivrosEmMemoria(validarUnidade(unidadeDeTrabalho));
    }

    @Override
    public RepositorioDeMovimentacao criarRepositorioDeMovimentacao(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioDeMovimentacaoEmMemoria(validarUnidade(unidadeDeTrabalho));
    }

    @Override
    public RepositorioDeRelacionamento criarRepositorioDeRelacionamento(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioDeRelacionamentoEmMemoria(validarUnidade(unidadeDeTrabalho));
    }

    private UnidadeDeTrabalhoEmMemoria validarUnidade(UnidadeDeTrabalho unidadeDeTrabalho) {
        Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho é obrigatória.");
        if (unidadeDeTrabalho instanceof UnidadeDeTrabalhoEmMemoria memoria) {
            return memoria;
        }
        throw new FamiliaIncompativelException(nomeDaFamilia(), unidadeDeTrabalho);
    }
}
