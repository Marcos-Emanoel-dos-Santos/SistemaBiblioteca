package br.pucpr.biblioteca.financeiro.politica;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <b>R02 — Strategy: ConcreteStrategy (política com teto de R$ 20,00).</b>
 * Cobra R$ 1,00 por dia corrido de atraso até o limite máximo de R$ 20,00.
 */
public final class PoliticaComTeto implements PoliticaDeMulta {

    private static final BigDecimal VALOR_POR_DIA = new BigDecimal("1.00");
    private static final BigDecimal TETO = new BigDecimal("20.00");

    @Override
    public String nome() {
        return "COM_TETO_20";
    }

    @Override
    public BigDecimal calcular(long diasDeAtraso) {
        if (diasDeAtraso <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal bruto = VALOR_POR_DIA.multiply(BigDecimal.valueOf(diasDeAtraso));
        return bruto.min(TETO).setScale(2, RoundingMode.HALF_UP);
    }
}
