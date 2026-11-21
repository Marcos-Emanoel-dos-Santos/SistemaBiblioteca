package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>R08 — Abstract Factory: produto concreto "unidade de trabalho" da família em memória.</b>
 * <p>
 * Coordena os repositórios em memória registrados nela: ao iniciar uma transação, tira uma
 * cópia consistente do estado de cada repositório; em caso de {@link #desfazer()}, restaura
 * todos os repositórios ao estado anterior, simulando rollback real.
 */
public final class UnidadeDeTrabalhoEmMemoria implements UnidadeDeTrabalho {

    private final List<ParticipanteTransacional> participantes = new ArrayList<>();
    private boolean emAndamento;

    void registrar(ParticipanteTransacional participante) {
        participantes.add(participante);
    }

    @Override
    public void iniciar() {
        if (emAndamento) {
            throw new IllegalStateException("Já existe uma transação em andamento nesta unidade de trabalho.");
        }
        for (ParticipanteTransacional participante : participantes) {
            participante.capturarEstado();
        }
        emAndamento = true;
    }

    @Override
    public void confirmar() {
        if (!emAndamento) {
            throw new IllegalStateException("Não há transação em andamento para confirmar.");
        }
        for (ParticipanteTransacional participante : participantes) {
            participante.confirmarEstado();
        }
        emAndamento = false;
    }

    @Override
    public void desfazer() {
        if (!emAndamento) {
            return;
        }
        for (ParticipanteTransacional participante : participantes) {
            participante.restaurarEstado();
        }
        emAndamento = false;
    }

    @Override
    public boolean emAndamento() {
        return emAndamento;
    }
}
