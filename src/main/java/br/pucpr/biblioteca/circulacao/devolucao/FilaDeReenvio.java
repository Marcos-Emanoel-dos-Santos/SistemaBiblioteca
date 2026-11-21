package br.pucpr.biblioteca.circulacao.devolucao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registra falhas isoladas de ouvintes (ex.: erro no envio de e-mail) para reprocessamento
 * posterior, sem interromper os demais ouvintes nem desfazer a devolução confirmada (R05).
 */
public final class FilaDeReenvio {

    public record FalhaRegistrada(String nomeOuvinte, EventoDevolucao evento, String motivo) {
    }

    private final List<FalhaRegistrada> falhas = new ArrayList<>();

    public void registrarFalha(String nomeOuvinte, EventoDevolucao evento, Throwable erro) {
        falhas.add(new FalhaRegistrada(
                nomeOuvinte, evento, erro.getMessage() != null ? erro.getMessage() : erro.getClass().getSimpleName()));
    }

    public List<FalhaRegistrada> listarFalhas() {
        return Collections.unmodifiableList(falhas);
    }

    public boolean vazia() {
        return falhas.isEmpty();
    }
}
