package br.pucpr.biblioteca;

import br.pucpr.biblioteca.config.RelogioAjustavel;
import br.pucpr.biblioteca.dto.ComprovanteDeMulta;
import br.pucpr.biblioteca.financeiro.ConfiguracaoAdministrativa;
import br.pucpr.biblioteca.financeiro.ServicoDeMultas;
import br.pucpr.biblioteca.financeiro.banco.AdaptadorBancoA;
import br.pucpr.biblioteca.financeiro.banco.AdaptadorBancoB;
import br.pucpr.biblioteca.financeiro.banco.BancoA;
import br.pucpr.biblioteca.financeiro.banco.BancoB;
import br.pucpr.biblioteca.financeiro.banco.SituacaoCobranca;
import br.pucpr.biblioteca.financeiro.politica.PoliticaOriginal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes do Requisito 3 — Adapter (integração com bancos incompatíveis). */
class R03_AdapterTest {

    @Test
    void R03_seteReaisViram700CentavosNoBancoA() {
        BancoA bancoA = new BancoA();
        AdaptadorBancoA adaptadorA = new AdaptadorBancoA(bancoA);
        ServicoDeMultas servico = new ServicoDeMultas(
                new ConfiguracaoAdministrativa(new PoliticaOriginal()),
                adaptadorA,
                RelogioAjustavel.em(LocalDate.of(2026, 11, 20)));

        ComprovanteDeMulta multa = servico.solicitarMulta("111.222.333-44", "DEV-7A", 7);
        assertEquals(new BigDecimal("7.00"), multa.valor());

        BancoA.TituloBancoA tituloExterno = bancoA.buscarTitulo(multa.idCobranca());
        assertEquals(700L, tituloExterno.valorEmCentavos());
        assertEquals("11122233344", tituloExterno.cpfSomenteDigitos());
        assertEquals(SituacaoCobranca.PENDENTE, adaptadorA.consultarSituacao(multa.idCobranca()));
    }

    @Test
    void R03_seteReaisViram7DecimalNoBancoB() {
        BancoB bancoB = new BancoB();
        AdaptadorBancoB adaptadorB = new AdaptadorBancoB(bancoB);
        ServicoDeMultas servico = new ServicoDeMultas(
                new ConfiguracaoAdministrativa(new PoliticaOriginal()),
                adaptadorB,
                RelogioAjustavel.em(LocalDate.of(2026, 11, 20)));

        ComprovanteDeMulta multa = servico.solicitarMulta("11122233344", "DEV-7B", 7);
        assertEquals(new BigDecimal("7.00"), multa.valor());

        BancoB.CobrancaBancoB cobrancaExterna = bancoB.buscarCobranca(multa.idCobranca());
        assertEquals(new BigDecimal("7.00"), cobrancaExterna.valorDecimal());
        assertEquals("111.222.333-44", cobrancaExterna.documento());
        assertEquals(SituacaoCobranca.PENDENTE, adaptadorB.consultarSituacao(multa.idCobranca()));
    }

    @Test
    void R03_notificacaoRepetidaNaoGeraSegundaQuitacao() {
        BancoA bancoA = new BancoA();
        AdaptadorBancoA adaptadorA = new AdaptadorBancoA(bancoA);
        ServicoDeMultas servico = new ServicoDeMultas(
                new ConfiguracaoAdministrativa(new PoliticaOriginal()),
                adaptadorA,
                RelogioAjustavel.em(LocalDate.of(2026, 11, 20)));

        ComprovanteDeMulta multa = servico.solicitarMulta("111.222.333-44", "DEV-DUP", 7);
        BancoA.NotificacaoBancoA aviso = bancoA.simularPagamento(multa.idCobranca(), LocalDate.of(2026, 11, 21));

        assertTrue(adaptadorA.encaminharNotificacao(aviso, servico));
        // Segunda notificação idêntica não deve gerar segunda quitação
        assertFalse(adaptadorA.encaminharNotificacao(aviso, servico));

        assertEquals(1, servico.getTotalQuitacoesRegistradas());
        assertEquals("QUITADA", servico.consultarPorDevolucao("DEV-DUP").orElseThrow().situacao());
    }

    @Test
    void R03_notificacaoDeCadaBancoTraduzidaParaContratoInstitucional() {
        BancoA bancoA = new BancoA();
        AdaptadorBancoA adaptadorA = new AdaptadorBancoA(bancoA);
        ServicoDeMultas servicoA = new ServicoDeMultas(
                new ConfiguracaoAdministrativa(new PoliticaOriginal()),
                adaptadorA,
                RelogioAjustavel.em(LocalDate.of(2026, 11, 20)));
        ComprovanteDeMulta mA = servicoA.solicitarMulta("111.222.333-44", "DEV-A", 7);
        BancoA.NotificacaoBancoA notifA = bancoA.simularPagamento(mA.idCobranca(), LocalDate.of(2026, 11, 22));
        assertTrue(adaptadorA.encaminharNotificacao(notifA, servicoA));
        assertEquals(SituacaoCobranca.PAGA, adaptadorA.consultarSituacao(mA.idCobranca()));
        assertEquals("QUITADA", servicoA.consultarPorDevolucao("DEV-A").orElseThrow().situacao());

        BancoB bancoB = new BancoB();
        AdaptadorBancoB adaptadorB = new AdaptadorBancoB(bancoB);
        ServicoDeMultas servicoB = new ServicoDeMultas(
                new ConfiguracaoAdministrativa(new PoliticaOriginal()),
                adaptadorB,
                RelogioAjustavel.em(LocalDate.of(2026, 11, 20)));
        ComprovanteDeMulta mB = servicoB.solicitarMulta("111.222.333-44", "DEV-B", 7);
        BancoB.AvisoBancoB avisoB = bancoB.simularPagamento(mB.idCobranca(), LocalDate.of(2026, 11, 22));
        assertTrue(adaptadorB.encaminharNotificacao(avisoB, servicoB));
        assertEquals(SituacaoCobranca.PAGA, adaptadorB.consultarSituacao(mB.idCobranca()));
        assertEquals("QUITADA", servicoB.consultarPorDevolucao("DEV-B").orElseThrow().situacao());
    }
}
