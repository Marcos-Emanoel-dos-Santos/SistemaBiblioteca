package br.pucpr.biblioteca.circulacao.devolucao;

/** Consolida indicadores operacionais de devolução (alimentado pelo {@link OuvinteIndicadores}). */
public final class PainelDeIndicadores {

    private int totalDevolucoes;
    private int totalNoPrazo;
    private int totalAtrasadas;
    private long somaDiasDeAtraso;

    public void registrar(EventoDevolucao evento) {
        totalDevolucoes++;
        if (evento.diasDeAtraso() > 0) {
            totalAtrasadas++;
            somaDiasDeAtraso += evento.diasDeAtraso();
        } else {
            totalNoPrazo++;
        }
    }

    public int getTotalDevolucoes() {
        return totalDevolucoes;
    }

    public int getTotalNoPrazo() {
        return totalNoPrazo;
    }

    public int getTotalAtrasadas() {
        return totalAtrasadas;
    }

    public long getSomaDiasDeAtraso() {
        return somaDiasDeAtraso;
    }
}
