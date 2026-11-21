package br.pucpr.biblioteca.dto;

import java.time.LocalDate;

/** Comprovante de renovação (f4). */
public record ComprovanteDeRenovacao(String idEmprestimo, int idLivro, int matricula,
                                     int numRenovacoes, LocalDate novoVencimento, String estadoDoLivro) {
}
