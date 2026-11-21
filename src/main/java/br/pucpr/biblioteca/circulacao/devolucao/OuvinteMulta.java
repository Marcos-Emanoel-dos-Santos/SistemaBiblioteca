package br.pucpr.biblioteca.circulacao.devolucao;

import br.pucpr.biblioteca.interfaces.iMulta;

import java.util.Objects;

/**
 * <b>R05 — Observer: ConcreteObserver responsável por acionar o Financeiro quando há atraso.</b>
 * Devoluções no prazo ({@code diasDeAtraso == 0}) não geram multa.
 */
public final class OuvinteMulta implements OuvinteDeDevolucao {

    private final iMulta financeiro;

    public OuvinteMulta(iMulta financeiro) {
        this.financeiro = Objects.requireNonNull(financeiro, "Serviço de multas é obrigatório.");
    }

    @Override
    public void aoDevolver(EventoDevolucao evento) {
        if (evento.diasDeAtraso() > 0) {
            financeiro.solicitarMulta(
                    evento.cpfEstudante(), evento.idDevolucao(), evento.diasDeAtraso());
        }
    }
}
