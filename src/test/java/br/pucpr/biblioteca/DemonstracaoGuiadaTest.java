package br.pucpr.biblioteca;

import br.pucpr.biblioteca.cli.Console;
import br.pucpr.biblioteca.cli.DemonstracaoGuiada;
import br.pucpr.biblioteca.config.Configuracao;
import br.pucpr.biblioteca.config.MontadorDoSistema;
import br.pucpr.biblioteca.config.PerfilDeExecucao;
import br.pucpr.biblioteca.config.RelogioAjustavel;
import br.pucpr.biblioteca.persistencia.jdbc.UnidadeDeTrabalhoJdbc;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Valida a execução ponta a ponta da Demonstração Guiada nos perfis em memória e relacional (JDBC). */
class DemonstracaoGuiadaTest {

    @Test
    void demonstracaoGuiadaExecutaFluxoCompletoEmMemoria() {
        RelogioAjustavel relogio = RelogioAjustavel.em(LocalDate.of(2026, 11, 1));
        MontadorDoSistema.SistemaMontado sistema = MontadorDoSistema.montar(Configuracao.padraoMemoria(), relogio);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Console console = new Console(
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(buffer, true, StandardCharsets.UTF_8));

        new DemonstracaoGuiada(sistema, relogio, console).executar();

        String saida = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(saida.contains("FIM DA DEMONSTRACAO GUIADA"));
        assertTrue(saida.contains("QUITADA"));
        assertEquals("LIVRE", sistema.tratadorDeAcervo().consultarLivro(9001).estado());
        assertEquals(1, sistema.servicoDeMultas().getTotalQuitacoesRegistradas());
        assertEquals(LocalDate.of(2026, 11, 1), LocalDate.now(relogio));
    }

    @Test
    void demonstracaoGuiadaExecutaFluxoCompletoEmJdbcComBancoB() {
        Configuracao configJdbc = new Configuracao(
                PerfilDeExecucao.PRODUCAO,
                "COM_TOLERANCIA_2_DIAS",
                "BANCO_B",
                3,
                14,
                30,
                true,
                true,
                "jdbc:sqlite::memory:",
                "",
                "");
        RelogioAjustavel relogio = RelogioAjustavel.em(LocalDate.of(2026, 11, 1));
        MontadorDoSistema.SistemaMontado sistema = MontadorDoSistema.montar(configJdbc, relogio, true);

        UnidadeDeTrabalhoJdbc uow = (UnidadeDeTrabalhoJdbc) sistema.acessoADados().unidadeDeTrabalho();
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Console console = new Console(
                    new ByteArrayInputStream(new byte[0]),
                    new PrintStream(buffer, true, StandardCharsets.UTF_8));

            new DemonstracaoGuiada(sistema, relogio, console).executar();

            String saida = buffer.toString(StandardCharsets.UTF_8);
            assertTrue(saida.contains("FIM DA DEMONSTRACAO GUIADA"));
            assertTrue(saida.contains("3.00"));
            assertTrue(saida.contains("QUITADA"));
            assertEquals("LIVRE", sistema.tratadorDeAcervo().consultarLivro(9001).estado());
        } finally {
            uow.close();
        }
    }
}
