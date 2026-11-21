package br.pucpr.biblioteca;

import br.pucpr.biblioteca.apoio.CenarioDeTeste;
import br.pucpr.biblioteca.circulacao.devolucao.OuvinteDeDevolucao;
import br.pucpr.biblioteca.circulacao.devolucao.OuvinteEmail;
import br.pucpr.biblioteca.config.MontadorDoSistema;
import br.pucpr.biblioteca.dto.ComprovanteDeDevolucao;
import br.pucpr.biblioteca.dto.ComprovanteDeMulta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes do Requisito 5 — Observer (reações independentes à devolução). */
class R05_ObserverTest {

    @Test
    void R05_devolucaoAtrasadaAlimentaInspecaoMultaEIndicadores() {
        CenarioDeTeste cenario = CenarioDeTeste.criar(LocalDate.of(2026, 11, 1)).cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        s.tratadorDeEmprestimo().emprestarLivro(1, 1001); // vencimento em +14 dias (15/11)

        // Avança 19 dias -> 5 dias de atraso
        cenario.relogio().avancarDias(19);
        ComprovanteDeDevolucao devolucao = s.tratadorDeDevolucao().registrarDevolucao(1, 1001);

        assertEquals(5, devolucao.diasDeAtraso());
        assertTrue(s.agendaDeInspecao().contemLivroPendente(1));
        assertEquals(1, s.painelDeIndicadores().getTotalDevolucoes());
        assertEquals(1, s.painelDeIndicadores().getTotalAtrasadas());
        assertEquals(5, s.painelDeIndicadores().getSomaDiasDeAtraso());
        assertEquals(1, s.servicoDeEmail().listarEnviadas().size());

        ComprovanteDeMulta multa = s.tratadorDeDevolucao().consultarMultaPorDevolucao(devolucao.idDevolucao()).orElseThrow();
        assertEquals(new BigDecimal("5.00"), multa.valor());
    }

    @Test
    void R05_devolucaoNoPrazoNaoGeraMulta() {
        CenarioDeTeste cenario = CenarioDeTeste.criar(LocalDate.of(2026, 11, 1)).cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        s.tratadorDeEmprestimo().emprestarLivro(1, 1001);

        cenario.relogio().avancarDias(10); // dentro do prazo de 14 dias
        ComprovanteDeDevolucao devolucao = s.tratadorDeDevolucao().registrarDevolucao(1, 1001);

        assertEquals(0, devolucao.diasDeAtraso());
        assertTrue(s.agendaDeInspecao().contemLivroPendente(1));
        assertEquals(1, s.painelDeIndicadores().getTotalNoPrazo());
        assertTrue(s.tratadorDeDevolucao().consultarMultaPorDevolucao(devolucao.idDevolucao()).isEmpty());
        assertTrue(s.tratadorDeDevolucao().listarMultas().isEmpty());
    }

    @Test
    void R05_livroPermaneceDevolvidoAteInspecao() {
        CenarioDeTeste cenario = CenarioDeTeste.criar().cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        s.tratadorDeEmprestimo().emprestarLivro(1, 1001);
        s.tratadorDeDevolucao().registrarDevolucao(1, 1001);

        // Os ouvintes não alteram o estado do livro: ele permanece DEVOLVIDO até a inspeção
        assertEquals("DEVOLVIDO", s.tratadorDeAcervo().consultarLivro(1).estado());
        assertTrue(s.agendaDeInspecao().contemLivroPendente(1));

        s.tratadorDeAcervo().inspecionarLivro(1, true);
        assertEquals("LIVRE", s.tratadorDeAcervo().consultarLivro(1).estado());
        assertFalse(s.agendaDeInspecao().contemLivroPendente(1));
    }

    @Test
    void R05_falhaNoEmailNaoAfetaDemaisNemDesfazDevolucao() {
        CenarioDeTeste cenario = CenarioDeTeste.criar(LocalDate.of(2026, 11, 1)).cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        // Substitui o ouvinte de e-mail por um que falha propositalmente e adiciona um ouvinte depois dele
        s.publicadorDeDevolucao().listarOuvintes().stream()
                .filter(o -> o instanceof OuvinteEmail)
                .forEach(s.publicadorDeDevolucao()::remover);

        OuvinteDeDevolucao emailComFalha = new OuvinteEmail((dest, assunto, corpo) -> {
            throw new IllegalStateException("Servidor SMTP indisponível");
        });
        boolean[] ouvintePosteriorExecutado = {false};
        s.publicadorDeDevolucao().registrar(emailComFalha);
        s.publicadorDeDevolucao().registrar(evento -> ouvintePosteriorExecutado[0] = true);

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        s.tratadorDeEmprestimo().emprestarLivro(1, 1001);
        cenario.relogio().avancarDias(17); // 3 dias de atraso

        ComprovanteDeDevolucao devolucao = s.tratadorDeDevolucao().registrarDevolucao(1, 1001);

        // Devolução confirmada, demais ouvintes executados e falha registrada na fila de reenvio
        assertEquals("DEVOLVIDO", s.tratadorDeAcervo().consultarLivro(1).estado());
        assertTrue(ouvintePosteriorExecutado[0]);
        assertTrue(s.agendaDeInspecao().contemLivroPendente(1));
        assertTrue(s.tratadorDeDevolucao().consultarMultaPorDevolucao(devolucao.idDevolucao()).isPresent());
        assertEquals(1, s.filaDeReenvio().listarFalhas().size());
    }
}
