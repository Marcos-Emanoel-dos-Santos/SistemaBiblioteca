package br.pucpr.biblioteca.dto;

import java.time.LocalDate;

/** Comprovante de empréstimo efetivado (f3). */
public record ComprovanteDeEmprestimo(String idEmprestimo, int idLivro, String titulo, int matricula,
                                      LocalDate dataDoEmprestimo, LocalDate dataDeVencimento,
                                      String estadoDoLivro) {
}
