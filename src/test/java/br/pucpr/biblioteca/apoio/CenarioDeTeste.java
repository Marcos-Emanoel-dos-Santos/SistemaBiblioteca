package br.pucpr.biblioteca.apoio;

import br.pucpr.biblioteca.config.Configuracao;
import br.pucpr.biblioteca.config.MontadorDoSistema;
import br.pucpr.biblioteca.config.RelogioAjustavel;

import java.time.LocalDate;

/** Apoio para montar um sistema completo em memória com relógio controlado nos testes JUnit. */
public final class CenarioDeTeste {

    private final RelogioAjustavel relogio;
    private final MontadorDoSistema.SistemaMontado sistema;

    private CenarioDeTeste(RelogioAjustavel relogio, MontadorDoSistema.SistemaMontado sistema) {
        this.relogio = relogio;
        this.sistema = sistema;
    }

    public static CenarioDeTeste criar() {
        return criar(LocalDate.of(2026, 11, 1));
    }

    public static CenarioDeTeste criar(LocalDate dataInicial) {
        RelogioAjustavel relogio = RelogioAjustavel.em(dataInicial);
        MontadorDoSistema.SistemaMontado sistema = MontadorDoSistema.montar(Configuracao.padraoMemoria(), relogio);
        return new CenarioDeTeste(relogio, sistema);
    }

    public CenarioDeTeste cadastrarDadosBasicos() {
        sistema.tratadorDePessoa().inserirPessoa("Ana", "Silva", "111.222.333-44");
        sistema.tratadorDePessoa().inserirPessoa("Bruno", "Costa", "222.333.444-55");
        sistema.tratadorDeEstudante().inserirEstudante("111.222.333-44", 1001);
        sistema.tratadorDeAtendente().inserirAtendente("222.333.444-55", 501);
        sistema.tratadorDeAcervo().inserirLivro(1, "Padrões de Projeto GoF");
        return this;
    }

    public RelogioAjustavel relogio() {
        return relogio;
    }

    public MontadorDoSistema.SistemaMontado sistema() {
        return sistema;
    }
}
