package br.pucpr.biblioteca.dto;

import java.time.LocalDate;

/** Comprovante de devolução registrada (f5). */
public record ComprovanteDeDevolucao(String idDevolucao, int idLivro, String titulo, int matricula,
                                     String cpfEstudante, LocalDate dataDaDevolucao,
                                     LocalDate dataDeVencimento, long diasDeAtraso, String estadoDoLivro) {
}
