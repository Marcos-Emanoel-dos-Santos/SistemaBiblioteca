package br.pucpr.biblioteca.dominio.transacao;

import java.time.LocalDate;

/**
 * Reserva de um livro por um estudante (regra 5.3).
 * {@code dataDeVencimento} guarda o vencimento da reserva (entry/registrarVencimentoReserva).
 */
public class Reserva extends Transacao {

    public Reserva(String id, int idLivro, int matricula, LocalDate data, LocalDate vencimentoDaReserva) {
        super(id, idLivro, matricula, data, vencimentoDaReserva);
    }
}
