package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.circulacao.solicitacao.SolicitacaoDeCirculacao;
import br.pucpr.biblioteca.dto.ComprovanteDeEmprestimo;
import br.pucpr.biblioteca.dto.ComprovanteDeRenovacao;
import br.pucpr.biblioteca.dto.ComprovanteDeReserva;

/**
 * Contrato do componente Biblioteca para os serviços do Estudante (f1–f4) e execução de
 * solicitações como objetos (R06 — Command). Implementado por {@code TratadorDeEmprestimo}.
 */
public interface iEmprestimo {

    /** f1: solicitar reserva de livro. */
    ComprovanteDeReserva reservarLivro(int idLivro, int matricula);

    /** f2: consultar reserva de livro. */
    ComprovanteDeReserva consultarReserva(int idLivro, int matricula);

    /** f3: efetivar empréstimo de livro reservado. */
    ComprovanteDeEmprestimo emprestarLivro(int idLivro, int matricula);

    /** f4: renovar empréstimo de livro. */
    ComprovanteDeRenovacao renovarEmprestimo(int idLivro, int matricula);

    /** R06: executa uma solicitação (reserva ou renovação) criada pela interface/cliente. */
    <R> R executarSolicitacao(SolicitacaoDeCirculacao<R> solicitacao);
}
