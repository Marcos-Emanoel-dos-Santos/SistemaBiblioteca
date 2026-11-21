package br.pucpr.biblioteca.financeiro.politica;

import java.math.BigDecimal;

/**
 * <b>R02 — Strategy: papel Strategy.</b>
 * <p>
 * Define o contrato de cálculo de multa a partir dos dias corridos inteiros de atraso.
 * Novas políticas podem ser adicionadas criando novas implementações desta interface sem
 * modificar {@code ServicoDeMultas} nem a emissão de boletos.
 */
public interface PoliticaDeMulta {

    String nome();

    BigDecimal calcular(long diasDeAtraso);
}
