package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.circulacao.solicitacao.ExecutorDeSolicitacoes;
import br.pucpr.biblioteca.circulacao.solicitacao.SolicitacaoDeCirculacao;
import br.pucpr.biblioteca.circulacao.solicitacao.SolicitacaoDeRenovacao;
import br.pucpr.biblioteca.circulacao.solicitacao.SolicitacaoDeReserva;
import br.pucpr.biblioteca.dto.ComprovanteDeEmprestimo;
import br.pucpr.biblioteca.dto.ComprovanteDeRenovacao;
import br.pucpr.biblioteca.dto.ComprovanteDeReserva;
import br.pucpr.biblioteca.interfaces.iCirculacao;
import br.pucpr.biblioteca.interfaces.iEmprestimo;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

/**
 * Tratador dos serviços do Estudante (f1–f4 — implementa {@link iEmprestimo}).
 * <p>
 * Não contém regras de negócio nem condicionais de estado: delega solicitações de reserva e
 * renovação ao {@link ExecutorDeSolicitacoes} (R06 — Command) e operações de consulta/empréstimo
 * diretamente ao contrato {@link iCirculacao} (R04 — Facade).
 */
public class TratadorDeEmprestimo implements iEmprestimo {

    private final iCirculacao circulacao;
    private final ExecutorDeSolicitacoes executor;
    private final Clock relogio;

    public TratadorDeEmprestimo(iCirculacao circulacao, ExecutorDeSolicitacoes executor, Clock relogio) {
        this.circulacao = Objects.requireNonNull(circulacao, "Contrato de circulação é obrigatório.");
        this.executor = Objects.requireNonNull(executor, "Executor de solicitações é obrigatório.");
        this.relogio = Objects.requireNonNull(relogio, "Relógio é obrigatório.");
    }

    @Override
    public ComprovanteDeReserva reservarLivro(int idLivro, int matricula) {
        SolicitacaoDeReserva solicitacao = new SolicitacaoDeReserva(
                "SOL-RES-" + UUID.randomUUID(), "estudante:" + matricula, relogio.instant(), idLivro, matricula);
        return executor.executar(solicitacao);
    }

    @Override
    public ComprovanteDeReserva consultarReserva(int idLivro, int matricula) {
        return circulacao.consultarReserva(idLivro, matricula);
    }

    @Override
    public ComprovanteDeEmprestimo emprestarLivro(int idLivro, int matricula) {
        return circulacao.emprestarLivro(idLivro, matricula);
    }

    @Override
    public ComprovanteDeRenovacao renovarEmprestimo(int idLivro, int matricula) {
        SolicitacaoDeRenovacao solicitacao = new SolicitacaoDeRenovacao(
                "SOL-REN-" + UUID.randomUUID(), "estudante:" + matricula, relogio.instant(), idLivro, matricula);
        return executor.executar(solicitacao);
    }

    @Override
    public <R> R executarSolicitacao(SolicitacaoDeCirculacao<R> solicitacao) {
        return executor.executar(solicitacao);
    }
}
