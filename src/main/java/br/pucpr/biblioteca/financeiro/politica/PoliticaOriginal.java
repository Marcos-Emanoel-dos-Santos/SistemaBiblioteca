package br.pucpr.biblioteca.financeiro.politica;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <b>R02 — Strategy: ConcreteStrategy (política padrão original).</b>
 * Cobra R$ 1,00 por dia corrido de atraso; sem atraso retorna R$ 0,00.
 */
public final class PoliticaOriginal implements PoliticaDeMulta {

    private static final BigDecimal VALOR_POR_DIA = new BigDecimal("1.00");

    @Override
    public String nome() {
        return "ORIGINAL";
    }

    @Override
    public BigDecimal calcular(long diasDeAtraso) {
        if (diasDeAtraso <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return VALOR_POR_DIA.multiply(BigDecimal.valueOf(diasDeAtraso))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
