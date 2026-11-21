package br.pucpr.biblioteca;

import br.pucpr.biblioteca.config.RelogioAjustavel;
import br.pucpr.biblioteca.dto.ComprovanteDeMulta;
import br.pucpr.biblioteca.financeiro.ConfiguracaoAdministrativa;
import br.pucpr.biblioteca.financeiro.ServicoDeMultas;
import br.pucpr.biblioteca.financeiro.banco.AdaptadorBancoA;
import br.pucpr.biblioteca.financeiro.banco.BancoA;
import br.pucpr.biblioteca.financeiro.politica.PoliticaComTeto;
import br.pucpr.biblioteca.financeiro.politica.PoliticaComTolerancia;
import br.pucpr.biblioteca.financeiro.politica.PoliticaOriginal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Testes do Requisito 2 — Strategy (políticas de cálculo de multa). */
class R02_StrategyTest {

    @Test
    void R02_cincoDiasPoliticaOriginalCincoReais() {
        ConfiguracaoAdministrativa config = new ConfiguracaoAdministrativa(new PoliticaOriginal());
        ServicoDeMultas servico = new ServicoDeMultas(
                config, new AdaptadorBancoA(new BancoA()), RelogioAjustavel.em(LocalDate.of(2026, 11, 20)));

        ComprovanteDeMulta multa = servico.solicitarMulta("111.222.333-44", "DEV-1", 5);
        assertEquals(new BigDecimal("5.00"), multa.valor());
        assertEquals("ORIGINAL", multa.politicaAplicada());
    }

    @Test
    void R02_cincoDiasPoliticaToleranciaTresReais() {
        ConfiguracaoAdministrativa config = new ConfiguracaoAdministrativa(new PoliticaComTolerancia());
        ServicoDeMultas servico = new ServicoDeMultas(
                config, new AdaptadorBancoA(new BancoA()), RelogioAjustavel.em(LocalDate.of(2026, 11, 20)));

        ComprovanteDeMulta multa = servico.solicitarMulta("111.222.333-44", "DEV-2", 5);
        assertEquals(new BigDecimal("3.00"), multa.valor());
        assertEquals("COM_TOLERANCIA_2_DIAS", multa.politicaAplicada());
    }

    @Test
    void R02_semAtrasoValorZero() {
        assertEquals(new BigDecimal("0.00"), new PoliticaOriginal().calcular(0));
        assertEquals(new BigDecimal("0.00"), new PoliticaComTeto().calcular(0));
        assertEquals(new BigDecimal("0.00"), new PoliticaComTolerancia().calcular(0));
        assertEquals(new BigDecimal("0.00"), new PoliticaComTolerancia().calcular(2));
    }

    @Test
    void R02_politicaComTetoLimitaVinteReais() {
        ConfiguracaoAdministrativa config = new ConfiguracaoAdministrativa(new PoliticaComTeto());
        ServicoDeMultas servico = new ServicoDeMultas(
                config, new AdaptadorBancoA(new BancoA()), RelogioAjustavel.em(LocalDate.of(2026, 11, 20)));

        ComprovanteDeMulta multa = servico.solicitarMulta("111.222.333-44", "DEV-3", 45);
        assertEquals(new BigDecimal("20.00"), multa.valor());
        assertEquals("COM_TETO_20", multa.politicaAplicada());
    }

    @Test
    void R02_trocaDePoliticaNaoRecalculaMultaEmitida() {
        ConfiguracaoAdministrativa config = new ConfiguracaoAdministrativa(new PoliticaOriginal());
        ServicoDeMultas servico = new ServicoDeMultas(
                config, new AdaptadorBancoA(new BancoA()), RelogioAjustavel.em(LocalDate.of(2026, 11, 20)));

        // Emite com política original: 5 dias = R$ 5,00
        ComprovanteDeMulta emitida = servico.solicitarMulta("111.222.333-44", "DEV-10", 5);
        assertEquals(new BigDecimal("5.00"), emitida.valor());
        assertEquals("ORIGINAL", emitida.politicaAplicada());

        // Administração troca política para tolerância de 2 dias
        config.definirPoliticaVigente(new PoliticaComTolerancia());

        // Multa já emitida (2ª via / consulta) mantém valor e política gravados originalmente
        ComprovanteDeMulta segundaVia = servico.emitirSegundaVia("DEV-10");
        assertEquals(new BigDecimal("5.00"), segundaVia.valor());
        assertEquals("ORIGINAL", segundaVia.politicaAplicada());

        // Nova multa emitida após a troca usa a nova política: 5 dias = R$ 3,00
        ComprovanteDeMulta novaMulta = servico.solicitarMulta("111.222.333-44", "DEV-11", 5);
        assertEquals(new BigDecimal("3.00"), novaMulta.valor());
        assertEquals("COM_TOLERANCIA_2_DIAS", novaMulta.politicaAplicada());
    }
}
