package br.pucpr.biblioteca.circulacao.devolucao;

import java.util.Objects;

/** <b>R05 — Observer: ConcreteObserver responsável por notificar o estudante por e-mail.</b> */
public final class OuvinteEmail implements OuvinteDeDevolucao {

    private final ServicoDeEmail servicoDeEmail;

    public OuvinteEmail(ServicoDeEmail servicoDeEmail) {
        this.servicoDeEmail = Objects.requireNonNull(servicoDeEmail, "Serviço de e-mail é obrigatório.");
    }

    @Override
    public void aoDevolver(EventoDevolucao evento) {
        String assunto = evento.diasDeAtraso() > 0
                ? "Devolução registrada com atraso — boleto de multa emitido"
                : "Confirmação de devolução de livro";
        String corpo = "Devolução " + evento.idDevolucao()
                + " do livro '" + evento.tituloLivro() + "' (id " + evento.idLivro()
                + ") registrada em " + evento.dataDaDevolucao()
                + ". Dias de atraso: " + evento.diasDeAtraso() + ".";
        servicoDeEmail.enviar(evento.cpfEstudante(), assunto, corpo);
    }
}
