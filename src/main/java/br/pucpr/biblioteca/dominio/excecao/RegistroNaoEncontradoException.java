package br.pucpr.biblioteca.dominio.excecao;

/**
 * Lançada quando a operação exige um registro que não existe
 * (regras 1.2–4.3 do Trabalho 02: "[se não estiver cadastrado], encerrar o procedimento").
 */
public class RegistroNaoEncontradoException extends RegraDeNegocioException {

    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
