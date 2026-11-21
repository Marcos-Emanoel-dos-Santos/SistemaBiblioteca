package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dominio.excecao.DadosInvalidosException;

/** Validações reutilizáveis de entrada nos cadastros (R07). */
final class ValidadorDeDados {

    private ValidadorDeDados() {
    }

    static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo '" + campo + "' é obrigatório.");
        }
        return valor.trim();
    }

    static String exigirCpf(String cpf) {
        String limpo = exigirTexto(cpf, "CPF");
        String digitos = limpo.replaceAll("[.\\-\\s]", "");
        if (!digitos.matches("\\d{11}")) {
            throw new DadosInvalidosException("CPF inválido (deve conter 11 dígitos): " + cpf);
        }
        return limpo;
    }

    static void exigirIdentificadorPositivo(int id, String campo) {
        if (id <= 0) {
            throw new DadosInvalidosException("O campo '" + campo + "' deve ser positivo.");
        }
    }
}
