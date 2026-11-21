package br.pucpr.biblioteca.dto;

import br.pucpr.biblioteca.dominio.pessoa.Pessoa;

/** Dados de uma pessoa apresentados à interface. */
public record DadosPessoa(String cpf, String nome, String sobrenome) {

    public static DadosPessoa de(Pessoa pessoa) {
        return new DadosPessoa(pessoa.getCpf(), pessoa.getNome(), pessoa.getSobrenome());
    }
}
