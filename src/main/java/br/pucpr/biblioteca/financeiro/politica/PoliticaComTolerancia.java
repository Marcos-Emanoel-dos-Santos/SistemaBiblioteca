package br.pucpr.biblioteca.financeiro.politica;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <b>R02 — Strategy: ConcreteStrategy (política com tolerância de 2 dias).</b>
 * Os dois primeiros dias de atraso são isentos; cobra R$ 1,00 apenas pelos dias excedentes.
 */
public final class PoliticaComTolerancia implements PoliticaDeMulta {

    private static final long DIAS_TOLERANCIA = 2L;
    private static final BigDecimal VALOR_POR_DIA = new BigDecimal("1.00");

    @Override
    public String nome() {
        return "COM_TOLERANCIA_2_DIAS";
    }

    @Override
    public BigDecimal calcular(long diasDeAtraso) {
        long diasCobraveis = Math.max(0L, diasDeAtraso - DIAS_TOLERANCIA);
        if (diasCobraveis == 0L) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return VALOR_POR_DIA.multiply(BigDecimal.valueOf(diasCobraveis))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
