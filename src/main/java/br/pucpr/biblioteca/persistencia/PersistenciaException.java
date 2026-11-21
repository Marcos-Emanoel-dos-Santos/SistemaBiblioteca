package br.pucpr.biblioteca.persistencia;

/** Falha técnica de persistência (ex.: {@code SQLException} encapsulada). */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
