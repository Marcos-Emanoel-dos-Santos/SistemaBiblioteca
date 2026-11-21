package br.pucpr.biblioteca.dominio.excecao;

/**
 * Base de todas as violações de regra de negócio do sistema.
 * <p>
 * É uma exceção não verificada: a camada de interface apenas exibe a mensagem;
 * quem decide se a operação é válida é sempre o domínio.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
