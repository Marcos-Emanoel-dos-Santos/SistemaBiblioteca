package br.pucpr.biblioteca;

import br.pucpr.biblioteca.apoio.CenarioDeTeste;
import br.pucpr.biblioteca.circulacao.solicitacao.SolicitacaoDeRenovacao;
import br.pucpr.biblioteca.config.MontadorDoSistema;
import br.pucpr.biblioteca.dominio.excecao.RegraDeNegocioException;
import br.pucpr.biblioteca.dto.ComprovanteDeEmprestimo;
import br.pucpr.biblioteca.dto.ComprovanteDeRenovacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Testes do Requisito 6 — Command (solicitações de circulação como objetos idempotentes). */
class R06_CommandTest {

    @Test
    void R06_mesmaRenovacaoDuasVezesAcrescentaApenas30Dias() {
        CenarioDeTeste cenario = CenarioDeTeste.criar(LocalDate.of(2026, 11, 1)).cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        ComprovanteDeEmprestimo emp = s.tratadorDeEmprestimo().emprestarLivro(1, 1001);
        LocalDate vencimentoInicial = emp.dataDeVencimento();

        SolicitacaoDeRenovacao mesmaSolicitacao = new SolicitacaoDeRenovacao(
                "CMD-REN-001", "estudante:1001", cenario.relogio().instant(), 1, 1001);

        ComprovanteDeRenovacao primeira = s.tratadorDeEmprestimo().executarSolicitacao(mesmaSolicitacao);
        ComprovanteDeRenovacao repetida = s.tratadorDeEmprestimo().executarSolicitacao(mesmaSolicitacao);

        assertEquals(vencimentoInicial.plusDays(30), primeira.novoVencimento());
        assertEquals(primeira, repetida);
        assertEquals(1, s.acessoADados().movimentacao().buscarEmprestimo(1, 1001).orElseThrow().getNumRenovacoes());
    }

    @Test
    void R06_solicitacoesDiferentesRespeitamLimiteDeDuasRenovacoes() {
        CenarioDeTeste cenario = CenarioDeTeste.criar(LocalDate.of(2026, 11, 1)).cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        ComprovanteDeEmprestimo emp = s.tratadorDeEmprestimo().emprestarLivro(1, 1001);

        SolicitacaoDeRenovacao s1 = new SolicitacaoDeRenovacao("REN-1", "estudante:1001", cenario.relogio().instant(), 1, 1001);
        SolicitacaoDeRenovacao s2 = new SolicitacaoDeRenovacao("REN-2", "estudante:1001", cenario.relogio().instant(), 1, 1001);
        SolicitacaoDeRenovacao s3 = new SolicitacaoDeRenovacao("REN-3", "estudante:1001", cenario.relogio().instant(), 1, 1001);

        ComprovanteDeRenovacao r1 = s.tratadorDeEmprestimo().executarSolicitacao(s1);
        ComprovanteDeRenovacao r2 = s.tratadorDeEmprestimo().executarSolicitacao(s2);

        assertEquals(emp.dataDeVencimento().plusDays(30), r1.novoVencimento());
        assertEquals(emp.dataDeVencimento().plusDays(60), r2.novoVencimento());
        assertThrows(RegraDeNegocioException.class, () -> s.tratadorDeEmprestimo().executarSolicitacao(s3));
    }

    @Test
    void R06_submissoesSimultaneasMesmoIdExecutamUmaVez() throws Exception {
        CenarioDeTeste cenario = CenarioDeTeste.criar(LocalDate.of(2026, 11, 1)).cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        ComprovanteDeEmprestimo emp = s.tratadorDeEmprestimo().emprestarLivro(1, 1001);

        SolicitacaoDeRenovacao cmdA = new SolicitacaoDeRenovacao(
                "CMD-CONCORRENTE-1", "tela-web", cenario.relogio().instant(), 1, 1001);
        SolicitacaoDeRenovacao cmdB = new SolicitacaoDeRenovacao(
                "CMD-CONCORRENTE-1", "tela-mobile", cenario.relogio().instant(), 1, 1001);

        CountDownLatch largada = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<ComprovanteDeRenovacao> f1 = pool.submit(() -> {
                largada.await();
                return s.tratadorDeEmprestimo().executarSolicitacao(cmdA);
            });
            Future<ComprovanteDeRenovacao> f2 = pool.submit(() -> {
                largada.await();
                return s.tratadorDeEmprestimo().executarSolicitacao(cmdB);
            });

            largada.countDown();
            ComprovanteDeRenovacao res1 = f1.get();
            ComprovanteDeRenovacao res2 = f2.get();

            assertEquals(res1, res2);
            assertEquals(1, res1.numRenovacoes());
            assertEquals(emp.dataDeVencimento().plusDays(30), res1.novoVencimento());
            assertEquals(1, s.acessoADados().movimentacao().buscarEmprestimo(1, 1001).orElseThrow().getNumRenovacoes());
        } finally {
            pool.shutdownNow();
        }
    }
}
