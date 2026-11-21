package br.pucpr.biblioteca.circulacao.devolucao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Contrato simples de envio de e-mail usado pelo {@link OuvinteEmail} (R05). */
public interface ServicoDeEmail {

    record MensagemEnviada(String destinatarioCpf, String assunto, String corpo) {
    }

    void enviar(String destinatarioCpf, String assunto, String corpo);

    /** Implementação padrão em memória que registra os e-mails disparados. */
    final class EmMemoria implements ServicoDeEmail {

        private final List<MensagemEnviada> enviadas = new ArrayList<>();

        @Override
        public void enviar(String destinatarioCpf, String assunto, String corpo) {
            enviadas.add(new MensagemEnviada(destinatarioCpf, assunto, corpo));
        }

        public List<MensagemEnviada> listarEnviadas() {
            return Collections.unmodifiableList(enviadas);
        }
    }
}
