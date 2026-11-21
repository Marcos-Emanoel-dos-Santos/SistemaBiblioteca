package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.dominio.transacao.Devolucao;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;
import br.pucpr.biblioteca.dominio.transacao.Reserva;
import br.pucpr.biblioteca.persistencia.RepositorioDeMovimentacao;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * <b>R08 — Abstract Factory: produto concreto "repositório de Movimentacao" (família em memória).</b>
 * Mantém as três associações distintas: reservas, empréstimos e devoluções.
 */
public final class RepositorioDeMovimentacaoEmMemoria implements RepositorioDeMovimentacao, ParticipanteTransacional {

    private final UnidadeDeTrabalhoEmMemoria unidadeDeTrabalho;
    private Map<String, Reserva> reservas = new LinkedHashMap<>();
    private Map<String, Emprestimo> emprestimos = new LinkedHashMap<>();
    private List<Devolucao> devolucoes = new ArrayList<>();

    private Map<String, Reserva> snapshotReservas;
    private Map<String, Emprestimo> snapshotEmprestimos;
    private List<Devolucao> snapshotDevolucoes;

    public RepositorioDeMovimentacaoEmMemoria(UnidadeDeTrabalhoEmMemoria unidadeDeTrabalho) {
        this.unidadeDeTrabalho = Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho é obrigatória.");
        this.unidadeDeTrabalho.registrar(this);
    }

    @Override
    public UnidadeDeTrabalho unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvarReserva(Reserva reserva) {
        Objects.requireNonNull(reserva, "Reserva não pode ser nula.");
        reservas.put(reserva.getId(), copiar(reserva));
    }

    @Override
    public Optional<Reserva> buscarReserva(int idLivro, int matricula) {
        for (Reserva reserva : reservas.values()) {
            if (reserva.getIdLivro() == idLivro && reserva.getMatricula() == matricula) {
                return Optional.of(copiar(reserva));
            }
        }
        return Optional.empty();
    }

    @Override
    public void removerReserva(String idReserva) {
        reservas.remove(idReserva);
    }

    @Override
    public void salvarEmprestimo(Emprestimo emprestimo) {
        Objects.requireNonNull(emprestimo, "Empréstimo não pode ser nulo.");
        emprestimos.put(emprestimo.getId(), copiar(emprestimo));
    }

    @Override
    public Optional<Emprestimo> buscarEmprestimo(int idLivro, int matricula) {
        for (Emprestimo emprestimo : emprestimos.values()) {
            if (emprestimo.getIdLivro() == idLivro && emprestimo.getMatricula() == matricula) {
                return Optional.of(copiar(emprestimo));
            }
        }
        return Optional.empty();
    }

    @Override
    public void removerEmprestimo(String idEmprestimo) {
        emprestimos.remove(idEmprestimo);
    }

    @Override
    public void salvarDevolucao(Devolucao devolucao) {
        Objects.requireNonNull(devolucao, "Devolução não pode ser nula.");
        for (int i = 0; i < devolucoes.size(); i++) {
            if (devolucoes.get(i).getId().equals(devolucao.getId())) {
                devolucoes.set(i, copiar(devolucao));
                return;
            }
        }
        devolucoes.add(copiar(devolucao));
    }

    @Override
    public Optional<Devolucao> buscarDevolucao(int idLivro, int matricula) {
        for (int i = devolucoes.size() - 1; i >= 0; i--) {
            Devolucao devolucao = devolucoes.get(i);
            if (devolucao.getIdLivro() == idLivro && devolucao.getMatricula() == matricula) {
                return Optional.of(copiar(devolucao));
            }
        }
        return Optional.empty();
    }

    @Override
    public void capturarEstado() {
        snapshotReservas = new LinkedHashMap<>();
        for (Map.Entry<String, Reserva> entry : reservas.entrySet()) {
            snapshotReservas.put(entry.getKey(), copiar(entry.getValue()));
        }
        snapshotEmprestimos = new LinkedHashMap<>();
        for (Map.Entry<String, Emprestimo> entry : emprestimos.entrySet()) {
            snapshotEmprestimos.put(entry.getKey(), copiar(entry.getValue()));
        }
        snapshotDevolucoes = new ArrayList<>(devolucoes.size());
        for (Devolucao devolucao : devolucoes) {
            snapshotDevolucoes.add(copiar(devolucao));
        }
    }

    @Override
    public void confirmarEstado() {
        snapshotReservas = null;
        snapshotEmprestimos = null;
        snapshotDevolucoes = null;
    }

    @Override
    public void restaurarEstado() {
        if (snapshotReservas != null) {
            reservas = snapshotReservas;
            emprestimos = snapshotEmprestimos;
            devolucoes = snapshotDevolucoes;
            snapshotReservas = null;
            snapshotEmprestimos = null;
            snapshotDevolucoes = null;
        }
    }

    private static Reserva copiar(Reserva r) {
        return new Reserva(r.getId(), r.getIdLivro(), r.getMatricula(), r.getData(), r.getDataDeVencimento());
    }

    private static Emprestimo copiar(Emprestimo e) {
        return new Emprestimo(e.getId(), e.getIdLivro(), e.getMatricula(), e.getData(),
                e.getDataDeVencimento(), e.getNumRenovacoes());
    }

    private static Devolucao copiar(Devolucao d) {
        return new Devolucao(d.getId(), d.getIdLivro(), d.getMatricula(), d.getData(), d.getDataDeVencimento());
    }
}
