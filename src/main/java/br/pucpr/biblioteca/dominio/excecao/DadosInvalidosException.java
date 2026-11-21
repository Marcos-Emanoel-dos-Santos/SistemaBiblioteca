package br.pucpr.biblioteca.dominio.excecao;

/** Lançada quando os dados informados não passam na validação (campos vazios, CPF inválido etc.). */
public class DadosInvalidosException extends RegraDeNegocioException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
