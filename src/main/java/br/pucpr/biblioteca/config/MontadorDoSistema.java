package br.pucpr.biblioteca.config;

import br.pucpr.biblioteca.cadastro.CadastroDeAtendente;
import br.pucpr.biblioteca.cadastro.CadastroDeEstudante;
import br.pucpr.biblioteca.cadastro.CadastroDeLivro;
import br.pucpr.biblioteca.cadastro.CadastroDePessoa;
import br.pucpr.biblioteca.circulacao.FachadaDeCirculacao;
import br.pucpr.biblioteca.circulacao.devolucao.AgendaDeInspecao;
import br.pucpr.biblioteca.circulacao.devolucao.FilaDeReenvio;
import br.pucpr.biblioteca.circulacao.devolucao.OuvinteEmail;
import br.pucpr.biblioteca.circulacao.devolucao.OuvinteIndicadores;
import br.pucpr.biblioteca.circulacao.devolucao.OuvinteInspecao;
import br.pucpr.biblioteca.circulacao.devolucao.OuvinteMulta;
import br.pucpr.biblioteca.circulacao.devolucao.PainelDeIndicadores;
import br.pucpr.biblioteca.circulacao.devolucao.PublicadorDeDevolucao;
import br.pucpr.biblioteca.circulacao.devolucao.ServicoDeEmail;
import br.pucpr.biblioteca.circulacao.solicitacao.ExecutorDeSolicitacoes;
import br.pucpr.biblioteca.consulta.ConsultaAoAcervo;
import br.pucpr.biblioteca.consulta.ConsultaComCache;
import br.pucpr.biblioteca.consulta.ConsultaComMedicao;
import br.pucpr.biblioteca.consulta.RegistroDeMedicoes;
import br.pucpr.biblioteca.consulta.ServicoDeConsultaAoAcervo;
import br.pucpr.biblioteca.dominio.excecao.DadosInvalidosException;
import br.pucpr.biblioteca.financeiro.ConfiguracaoAdministrativa;
import br.pucpr.biblioteca.financeiro.ServicoDeMultas;
import br.pucpr.biblioteca.financeiro.banco.AdaptadorBancoA;
import br.pucpr.biblioteca.financeiro.banco.AdaptadorBancoB;
import br.pucpr.biblioteca.financeiro.banco.BancoA;
import br.pucpr.biblioteca.financeiro.banco.BancoB;
import br.pucpr.biblioteca.financeiro.politica.PoliticaComTeto;
import br.pucpr.biblioteca.financeiro.politica.PoliticaComTolerancia;
import br.pucpr.biblioteca.financeiro.politica.PoliticaDeMulta;
import br.pucpr.biblioteca.financeiro.politica.PoliticaOriginal;
import br.pucpr.biblioteca.interfaces.iCirculacao;
import br.pucpr.biblioteca.interfaces.iCobranca;
import br.pucpr.biblioteca.nucleo.AcervoDeLivros;
import br.pucpr.biblioteca.nucleo.Movimentacao;
import br.pucpr.biblioteca.nucleo.Relacionamento;
import br.pucpr.biblioteca.organizacao.OrganizacaoDoAcervo;
import br.pucpr.biblioteca.persistencia.AcessoADados;
import br.pucpr.biblioteca.persistencia.FabricaDeAcessoADados;
import br.pucpr.biblioteca.persistencia.jdbc.FabricaRelacionalMySQL;
import br.pucpr.biblioteca.persistencia.jdbc.UnidadeDeTrabalhoJdbc;
import br.pucpr.biblioteca.persistencia.memoria.FabricaEmMemoria;
import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratadores.TratadorDeEmprestimo;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;

import java.time.Clock;
import java.util.Objects;

/**
 * <b>Composition Root</b> do sistema.
 * <p>
 * Único local que escolhe:
 * <ul>
 *   <li>a {@link FabricaDeAcessoADados} pelo {@link PerfilDeExecucao} (R08 — Abstract Factory);</li>
 *   <li>a {@link PoliticaDeMulta} inicial na {@link ConfiguracaoAdministrativa} (R02 — Strategy);</li>
 *   <li>o adaptador de cobrança ({@link AdaptadorBancoA} ou {@link AdaptadorBancoB}) (R03 — Adapter);</li>
 *   <li>os ouvintes registrados no {@link PublicadorDeDevolucao} (R05 — Observer);</li>
 *   <li>a cadeia de decoradores {@code Medicao(Cache(Original))} da consulta (R09 — Decorator).</li>
 * </ul>
 */
public final class MontadorDoSistema {

    public record SistemaMontado(Configuracao configuracao,
                                 Clock relogio,
                                 FabricaDeAcessoADados fabricaDeDados,
                                 AcessoADados acessoADados,
                                 TratadorDePessoa tratadorDePessoa,
                                 TratadorDeEstudante tratadorDeEstudante,
                                 TratadorDeAtendente tratadorDeAtendente,
                                 TratadorDeAcervo tratadorDeAcervo,
                                 TratadorDeEmprestimo tratadorDeEmprestimo,
                                 TratadorDeDevolucao tratadorDeDevolucao,
                                 ConfiguracaoAdministrativa configuracaoAdministrativa,
                                 ServicoDeMultas servicoDeMultas,
                                 PublicadorDeDevolucao publicadorDeDevolucao,
                                 AgendaDeInspecao agendaDeInspecao,
                                 PainelDeIndicadores painelDeIndicadores,
                                 FilaDeReenvio filaDeReenvio,
                                 ServicoDeEmail.EmMemoria servicoDeEmail,
                                 ExecutorDeSolicitacoes executorDeSolicitacoes,
                                 ConsultaAoAcervo consultaOriginal,
                                 ConsultaComCache consultaComCache,
                                 RegistroDeMedicoes registroDeMedicoes,
                                 OrganizacaoDoAcervo organizacaoDoAcervo,
                                 BancoA bancoA,
                                 AdaptadorBancoA adaptadorBancoA,
                                 BancoB bancoB,
                                 AdaptadorBancoB adaptadorBancoB) {
    }

    private MontadorDoSistema() {
    }

    public static SistemaMontado montar(Configuracao configuracao, Clock relogio) {
        return montar(configuracao, relogio, false);
    }

    public static SistemaMontado montar(Configuracao configuracao, Clock relogio, boolean inicializarEsquemaJdbc) {
        Objects.requireNonNull(configuracao, "Configuração é obrigatória.");
        Objects.requireNonNull(relogio, "Relógio é obrigatório.");

        // 1. R08 — Abstract Factory: escolha única da família pelo perfil
        FabricaDeAcessoADados fabrica = escolherFabrica(configuracao);
        AcessoADados acesso = AcessoADados.criarCom(fabrica);
        if (inicializarEsquemaJdbc && acesso.unidadeDeTrabalho() instanceof UnidadeDeTrabalhoJdbc uowJdbc) {
            uowJdbc.inicializarEsquema();
        }

        // 2. Núcleo (pontos internos)
        AcervoDeLivros acervo = new AcervoDeLivros(acesso.livros());
        Relacionamento relacionamento = new Relacionamento(acesso.relacionamento());
        Movimentacao movimentacao = new Movimentacao(acesso.movimentacao());

        // 3. R02 Strategy + R03 Adapter (Financeiro + Banco externo)
        ConfiguracaoAdministrativa configAdm = new ConfiguracaoAdministrativa(
                escolherPolitica(configuracao.politicaDeMulta()));
        BancoA bancoA = new BancoA();
        AdaptadorBancoA adaptadorBancoA = new AdaptadorBancoA(bancoA);
        BancoB bancoB = new BancoB();
        AdaptadorBancoB adaptadorBancoB = new AdaptadorBancoB(bancoB);
        iCobranca cobrancaAtiva = escolherCobranca(configuracao.bancoCobranca(), adaptadorBancoA, adaptadorBancoB);

        ServicoDeMultas servicoDeMultas = new ServicoDeMultas(configAdm, cobrancaAtiva, relogio);

        // 4. R04 Facade + R06 Command
        iCirculacao fachada = new FachadaDeCirculacao(
                acesso.unidadeDeTrabalho(), acervo, relacionamento, movimentacao, relogio,
                configuracao.diasValidadeReserva(), configuracao.diasPrazoEmprestimo(), configuracao.diasRenovacao());
        ExecutorDeSolicitacoes executor = new ExecutorDeSolicitacoes(fachada);

        // 5. R05 Observer (Devolução)
        FilaDeReenvio filaDeReenvio = new FilaDeReenvio();
        PublicadorDeDevolucao publicador = new PublicadorDeDevolucao(filaDeReenvio);
        AgendaDeInspecao agendaDeInspecao = new AgendaDeInspecao();
        PainelDeIndicadores painelDeIndicadores = new PainelDeIndicadores();
        ServicoDeEmail.EmMemoria servicoDeEmail = new ServicoDeEmail.EmMemoria();

        publicador.registrar(new OuvinteInspecao(agendaDeInspecao));
        publicador.registrar(new OuvinteMulta(servicoDeMultas));
        publicador.registrar(new OuvinteIndicadores(painelDeIndicadores));
        publicador.registrar(new OuvinteEmail(servicoDeEmail));

        // 6. R09 Decorator (Consulta pública: Medicao(Cache(Original)))
        ConsultaAoAcervo consultaOriginal = new ConsultaAoAcervo(acervo);
        ServicoDeConsultaAoAcervo cadeiaConsulta = consultaOriginal;
        ConsultaComCache consultaComCache = null;
        if (configuracao.ativarCacheConsulta()) {
            consultaComCache = new ConsultaComCache(cadeiaConsulta);
            cadeiaConsulta = consultaComCache;
        }
        RegistroDeMedicoes registroDeMedicoes = new RegistroDeMedicoes();
        if (configuracao.ativarMedicaoConsulta()) {
            cadeiaConsulta = new ConsultaComMedicao(cadeiaConsulta, registroDeMedicoes);
        }

        // 7. R10 Composite (Organização hierárquica)
        OrganizacaoDoAcervo organizacao = new OrganizacaoDoAcervo();

        // 8. R07 Template Method + Tratadores
        TratadorDePessoa tratadorPessoa = new TratadorDePessoa(
                new CadastroDePessoa(acesso.unidadeDeTrabalho(), relacionamento), relacionamento);
        TratadorDeEstudante tratadorEstudante = new TratadorDeEstudante(
                new CadastroDeEstudante(acesso.unidadeDeTrabalho(), relacionamento), relacionamento);
        TratadorDeAtendente tratadorAtendente = new TratadorDeAtendente(
                new CadastroDeAtendente(acesso.unidadeDeTrabalho(), relacionamento), relacionamento);
        TratadorDeAcervo tratadorAcervo = new TratadorDeAcervo(
                acesso.unidadeDeTrabalho(),
                new CadastroDeLivro(acesso.unidadeDeTrabalho(), acervo),
                acervo, cadeiaConsulta, organizacao, agendaDeInspecao);
        TratadorDeEmprestimo tratadorEmprestimo = new TratadorDeEmprestimo(fachada, executor, relogio);
        TratadorDeDevolucao tratadorDevolucao = new TratadorDeDevolucao(
                fachada, publicador, movimentacao, servicoDeMultas);

        return new SistemaMontado(
                configuracao, relogio, fabrica, acesso,
                tratadorPessoa, tratadorEstudante, tratadorAtendente,
                tratadorAcervo, tratadorEmprestimo, tratadorDevolucao,
                configAdm, servicoDeMultas, publicador, agendaDeInspecao,
                painelDeIndicadores, filaDeReenvio, servicoDeEmail, executor,
                consultaOriginal, consultaComCache, registroDeMedicoes, organizacao,
                bancoA, adaptadorBancoA, bancoB, adaptadorBancoB);
    }

    public static FabricaDeAcessoADados escolherFabrica(Configuracao configuracao) {
        return switch (configuracao.perfil()) {
            case MEMORIA -> new FabricaEmMemoria();
            case PRODUCAO -> new FabricaRelacionalMySQL(
                    configuracao.jdbcUrl(), configuracao.jdbcUsuario(), configuracao.jdbcSenha());
        };
    }

    public static PoliticaDeMulta escolherPolitica(String nome) {
        return switch (nome) {
            case "ORIGINAL" -> new PoliticaOriginal();
            case "COM_TETO_20", "TETO" -> new PoliticaComTeto();
            case "COM_TOLERANCIA_2_DIAS", "TOLERANCIA" -> new PoliticaComTolerancia();
            default -> throw new DadosInvalidosException("Política de multa desconhecida: " + nome);
        };
    }

    private static iCobranca escolherCobranca(String banco,
                                              AdaptadorBancoA adaptadorBancoA,
                                              AdaptadorBancoB adaptadorBancoB) {
        return switch (banco) {
            case "BANCO_A" -> adaptadorBancoA;
            case "BANCO_B" -> adaptadorBancoB;
            default -> throw new DadosInvalidosException("Banco de cobrança desconhecido: " + banco);
        };
    }
}
