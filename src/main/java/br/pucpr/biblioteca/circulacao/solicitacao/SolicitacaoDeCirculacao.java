package br.pucpr.biblioteca.circulacao.solicitacao;

import br.pucpr.biblioteca.interfaces.iCirculacao;

import java.time.Instant;

/**
 * <b>R06 — Command: papel Command.</b>
 * <p>
 * Encapsula uma solicitação de circulação (reserva ou renovação) com identificador único,
 * autor, instante de criação e operação de execução delegada ao receptor ({@link iCirculacao}).
 * Propositalmente não define operação de {@code desfazer()}, pois entregas físicas não são
 * revertidas automaticamente.
 */
public interface SolicitacaoDeCirculacao<R> {

    String getId();

    String getAutor();

    Instant getInstanteCriacao();

    R executar(iCirculacao circulacao);
}
