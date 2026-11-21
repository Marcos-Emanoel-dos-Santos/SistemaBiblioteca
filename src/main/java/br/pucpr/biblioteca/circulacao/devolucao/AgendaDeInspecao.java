package br.pucpr.biblioteca.circulacao.devolucao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mantém os exemplares devolvidos que aguardam decisão de inspeção física.
 * Apenas agenda a tarefa: não altera o estado {@code DEVOLVIDO} do {@code Livro}.
 */
public final class AgendaDeInspecao {

    private final List<EventoDevolucao> pendentes = new ArrayList<>();

    public void agendar(EventoDevolucao evento) {
        pendentes.add(evento);
    }

    public boolean contemLivroPendente(int idLivro) {
        return pendentes.stream().anyMatch(e -> e.idLivro() == idLivro);
    }

    public void concluirInspecao(int idLivro) {
        pendentes.removeIf(e -> e.idLivro() == idLivro);
    }

    public List<EventoDevolucao> listarPendentes() {
        return Collections.unmodifiableList(pendentes);
    }
}
