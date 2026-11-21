package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.dto.ComprovanteDeDevolucao;
import br.pucpr.biblioteca.dto.ComprovanteDeEmprestimo;
import br.pucpr.biblioteca.dto.ComprovanteDeRenovacao;
import br.pucpr.biblioteca.dto.ComprovanteDeReserva;

/**
 * <b>R04 — Facade: contrato de circulação.</b>
 * Entrada única para reserva, consulta de reserva, empréstimo, renovação e devolução,
 * devolvendo comprovantes imutáveis em vez de coleções internas.
 */
public interface iCirculacao {

    ComprovanteDeReserva reservarLivro(int idLivro, int matricula);

    ComprovanteDeReserva consultarReserva(int idLivro, int matricula);

    ComprovanteDeEmprestimo emprestarLivro(int idLivro, int matricula);

    ComprovanteDeRenovacao renovarEmprestimo(int idLivro, int matricula);

    ComprovanteDeDevolucao registrarDevolucao(int idLivro, int matricula);
}
