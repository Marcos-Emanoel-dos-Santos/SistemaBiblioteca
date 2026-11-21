package br.pucpr.biblioteca.dto;

/** Dados de um estudante apresentados à interface (com o nome da pessoa associada). */
public record DadosEstudante(int matricula, String cpf, String nomeCompleto) {
}
