package br.pucpr.biblioteca.circulacao.devolucao;

import java.util.Objects;

/** <b>R05 — Observer: ConcreteObserver responsável por atualizar o painel de indicadores.</b> */
public final class OuvinteIndicadores implements OuvinteDeDevolucao {

    private final PainelDeIndicadores painel;

    public OuvinteIndicadores(PainelDeIndicadores painel) {
        this.painel = Objects.requireNonNull(painel, "Painel de indicadores é obrigatório.");
    }

    @Override
    public void aoDevolver(EventoDevolucao evento) {
        painel.registrar(evento);
    }
}
