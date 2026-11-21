package br.pucpr.biblioteca.dto;

/** Dados de um atendente apresentados à interface (com o nome da pessoa associada). */
public record DadosAtendente(int idAtendente, String cpf, String nomeCompleto) {
}
