package br.pucpr.biblioteca.dto;

import java.time.LocalDate;

/** Comprovante de reserva (f1) devolvido pela fachada de circulação em vez de coleções internas. */
public record ComprovanteDeReserva(String idReserva, int idLivro, String titulo, int matricula,
                                   LocalDate dataDaReserva, LocalDate vencimentoDaReserva) {
}
