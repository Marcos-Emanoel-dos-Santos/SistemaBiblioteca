package br.pucpr.biblioteca.circulacao.devolucao;

import java.util.Objects;

/** <b>R05 — Observer: ConcreteObserver responsável por agendar a inspeção do exemplar devolvido.</b> */
public final class OuvinteInspecao implements OuvinteDeDevolucao {

    private final AgendaDeInspecao agenda;

    public OuvinteInspecao(AgendaDeInspecao agenda) {
        this.agenda = Objects.requireNonNull(agenda, "Agenda de inspeção é obrigatória.");
    }

    @Override
    public void aoDevolver(EventoDevolucao evento) {
        agenda.agendar(evento);
    }
}
