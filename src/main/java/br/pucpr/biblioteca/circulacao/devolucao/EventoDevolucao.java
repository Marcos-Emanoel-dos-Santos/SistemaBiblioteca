package br.pucpr.biblioteca.circulacao.devolucao;

import br.pucpr.biblioteca.dto.ComprovanteDeDevolucao;

import java.time.LocalDate;

/**
 * <b>R05 — Observer: evento imutável (modelo push).</b>
 * <p>
 * Carrega consigo todos os dados necessários para os ouvintes (vencimento, data da devolução,
 * dias de atraso, estudante/CPF), pois o empréstimo ativo já foi removido da {@code Movimentacao}.
 */
public record EventoDevolucao(String idDevolucao, int idLivro, String tituloLivro,
                              int matriculaEstudante, String cpfEstudante,
                              LocalDate dataDeVencimento, LocalDate dataDaDevolucao,
                              long diasDeAtraso) {

    public static EventoDevolucao de(ComprovanteDeDevolucao comprovante) {
        return new EventoDevolucao(
                comprovante.idDevolucao(),
                comprovante.idLivro(),
                comprovante.titulo(),
                comprovante.matricula(),
                comprovante.cpfEstudante(),
                comprovante.dataDeVencimento(),
                comprovante.dataDaDevolucao(),
                comprovante.diasDeAtraso());
    }
}
