package br.pucpr.biblioteca.circulacao.devolucao;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <b>R05 — Observer: papel Subject.</b>
 * <p>
 * Notifica cada ouvinte registrado após a confirmação da devolução. Exceções lançadas por um
 * ouvinte são capturadas e encaminhadas à {@link FilaDeReenvio}, garantindo que a falha de um
 * interessado não impeça os demais nem desfaça a devolução.
 */
public final class PublicadorDeDevolucao {

    private final List<OuvinteDeDevolucao> ouvintes = new CopyOnWriteArrayList<>();
    private final FilaDeReenvio filaDeReenvio;

    public PublicadorDeDevolucao(FilaDeReenvio filaDeReenvio) {
        this.filaDeReenvio = Objects.requireNonNull(filaDeReenvio, "Fila de reenvio é obrigatória.");
    }

    public void registrar(OuvinteDeDevolucao ouvinte) {
        Objects.requireNonNull(ouvinte, "Ouvinte não pode ser nulo.");
        if (!ouvintes.contains(ouvinte)) {
            ouvintes.add(ouvinte);
        }
    }

    public void remover(OuvinteDeDevolucao ouvinte) {
        ouvintes.remove(ouvinte);
    }

    public void publicar(EventoDevolucao evento) {
        Objects.requireNonNull(evento, "Evento de devolução é obrigatório.");
        for (OuvinteDeDevolucao ouvinte : ouvintes) {
            try {
                ouvinte.aoDevolver(evento);
            } catch (RuntimeException falha) {
                filaDeReenvio.registrarFalha(ouvinte.getClass().getSimpleName(), evento, falha);
            }
        }
    }

    public List<OuvinteDeDevolucao> listarOuvintes() {
        return List.copyOf(ouvintes);
    }
}
