package br.pucpr.biblioteca.dominio.excecao;

/**
 * Lançada quando um pré-requisito de uma operação não é atendido
 * (ex.: incluir estudante para um CPF que não está cadastrado como Pessoa — R07).
 */
public class PreRequisitoNaoAtendidoException extends RegraDeNegocioException {

    public PreRequisitoNaoAtendidoException(String mensagem) {
        super(mensagem);
    }
}
