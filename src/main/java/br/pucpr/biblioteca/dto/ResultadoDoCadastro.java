package br.pucpr.biblioteca.dto;

/**
 * Resultado da inclusão de um cadastro (passo "apresentar" do R07).
 *
 * @param dados        dados apresentados à interface
 * @param jaCadastrado {@code true} quando o registro já existia e foi apenas apresentado
 *                     (regras 1.1–4.1: "se já está cadastrado, mostrar os dados do cadastro")
 */
public record ResultadoDoCadastro<R>(R dados, boolean jaCadastrado) {

    public static <R> ResultadoDoCadastro<R> novo(R dados) {
        return new ResultadoDoCadastro<>(dados, false);
    }

    public static <R> ResultadoDoCadastro<R> existente(R dados) {
        return new ResultadoDoCadastro<>(dados, true);
    }

    public String mensagem() {
        return jaCadastrado ? "Já cadastrado: " + dados : "Cadastrado com sucesso: " + dados;
    }
}
