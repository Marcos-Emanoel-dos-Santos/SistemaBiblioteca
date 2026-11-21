package br.pucpr.biblioteca.persistencia.jdbc;

import br.pucpr.biblioteca.dominio.transacao.Devolucao;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;
import br.pucpr.biblioteca.dominio.transacao.Reserva;
import br.pucpr.biblioteca.persistencia.PersistenciaException;
import br.pucpr.biblioteca.persistencia.RepositorioDeMovimentacao;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * <b>R08 — Abstract Factory: produto concreto "repositório de Movimentacao" (família relacional JDBC).</b>
 */
public final class RepositorioDeMovimentacaoJdbc implements RepositorioDeMovimentacao {

    private final UnidadeDeTrabalhoJdbc unidadeDeTrabalho;

    public RepositorioDeMovimentacaoJdbc(UnidadeDeTrabalhoJdbc unidadeDeTrabalho) {
        this.unidadeDeTrabalho = Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho JDBC é obrigatória.");
    }

    @Override
    public UnidadeDeTrabalho unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvarReserva(Reserva reserva) {
        Objects.requireNonNull(reserva, "Reserva não pode ser nula.");
        Connection conn = unidadeDeTrabalho.obterConexao();
        try {
            int atualizados;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE reservas SET id_livro = ?, matricula = ?, data = ?, data_vencimento = ? WHERE id = ?")) {
                ps.setInt(1, reserva.getIdLivro());
                ps.setInt(2, reserva.getMatricula());
                ps.setString(3, reserva.getData().toString());
                ps.setString(4, reserva.getDataDeVencimento().toString());
                ps.setString(5, reserva.getId());
                atualizados = ps.executeUpdate();
            }
            if (atualizados == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO reservas (id, id_livro, matricula, data, data_vencimento) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setString(1, reserva.getId());
                    ps.setInt(2, reserva.getIdLivro());
                    ps.setInt(3, reserva.getMatricula());
                    ps.setString(4, reserva.getData().toString());
                    ps.setString(5, reserva.getDataDeVencimento().toString());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar reserva " + reserva.getId(), e);
        }
    }

    @Override
    public Optional<Reserva> buscarReserva(int idLivro, int matricula) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, id_livro, matricula, data, data_vencimento FROM reservas WHERE id_livro = ? AND matricula = ?")) {
            ps.setInt(1, idLivro);
            ps.setInt(2, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Reserva(
                            rs.getString("id"),
                            rs.getInt("id_livro"),
                            rs.getInt("matricula"),
                            LocalDate.parse(rs.getString("data")),
                            LocalDate.parse(rs.getString("data_vencimento"))));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar reserva do livro " + idLivro, e);
        }
    }

    @Override
    public void removerReserva(String idReserva) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM reservas WHERE id = ?")) {
            ps.setString(1, idReserva);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover reserva " + idReserva, e);
        }
    }

    @Override
    public void salvarEmprestimo(Emprestimo emprestimo) {
        Objects.requireNonNull(emprestimo, "Empréstimo não pode ser nulo.");
        Connection conn = unidadeDeTrabalho.obterConexao();
        try {
            int atualizados;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE emprestimos SET id_livro = ?, matricula = ?, data = ?, data_vencimento = ?, num_renovacoes = ? WHERE id = ?")) {
                ps.setInt(1, emprestimo.getIdLivro());
                ps.setInt(2, emprestimo.getMatricula());
                ps.setString(3, emprestimo.getData().toString());
                ps.setString(4, emprestimo.getDataDeVencimento().toString());
                ps.setInt(5, emprestimo.getNumRenovacoes());
                ps.setString(6, emprestimo.getId());
                atualizados = ps.executeUpdate();
            }
            if (atualizados == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO emprestimos (id, id_livro, matricula, data, data_vencimento, num_renovacoes) VALUES (?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, emprestimo.getId());
                    ps.setInt(2, emprestimo.getIdLivro());
                    ps.setInt(3, emprestimo.getMatricula());
                    ps.setString(4, emprestimo.getData().toString());
                    ps.setString(5, emprestimo.getDataDeVencimento().toString());
                    ps.setInt(6, emprestimo.getNumRenovacoes());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar empréstimo " + emprestimo.getId(), e);
        }
    }

    @Override
    public Optional<Emprestimo> buscarEmprestimo(int idLivro, int matricula) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, id_livro, matricula, data, data_vencimento, num_renovacoes FROM emprestimos WHERE id_livro = ? AND matricula = ?")) {
            ps.setInt(1, idLivro);
            ps.setInt(2, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Emprestimo(
                            rs.getString("id"),
                            rs.getInt("id_livro"),
                            rs.getInt("matricula"),
                            LocalDate.parse(rs.getString("data")),
                            LocalDate.parse(rs.getString("data_vencimento")),
                            rs.getInt("num_renovacoes")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar empréstimo do livro " + idLivro, e);
        }
    }

    @Override
    public void removerEmprestimo(String idEmprestimo) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM emprestimos WHERE id = ?")) {
            ps.setString(1, idEmprestimo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover empréstimo " + idEmprestimo, e);
        }
    }

    @Override
    public void salvarDevolucao(Devolucao devolucao) {
        Objects.requireNonNull(devolucao, "Devolução não pode ser nula.");
        Connection conn = unidadeDeTrabalho.obterConexao();
        try {
            int atualizados;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE devolucoes SET id_livro = ?, matricula = ?, data = ?, data_vencimento = ? WHERE id = ?")) {
                ps.setInt(1, devolucao.getIdLivro());
                ps.setInt(2, devolucao.getMatricula());
                ps.setString(3, devolucao.getData().toString());
                ps.setString(4, devolucao.getDataDeVencimento().toString());
                ps.setString(5, devolucao.getId());
                atualizados = ps.executeUpdate();
            }
            if (atualizados == 0) {
                int proximaOrdem = proximaOrdemDevolucao(conn);
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO devolucoes (id, id_livro, matricula, data, data_vencimento, ordem) VALUES (?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, devolucao.getId());
                    ps.setInt(2, devolucao.getIdLivro());
                    ps.setInt(3, devolucao.getMatricula());
                    ps.setString(4, devolucao.getData().toString());
                    ps.setString(5, devolucao.getDataDeVencimento().toString());
                    ps.setInt(6, proximaOrdem);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar devolução " + devolucao.getId(), e);
        }
    }

    @Override
    public Optional<Devolucao> buscarDevolucao(int idLivro, int matricula) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, id_livro, matricula, data, data_vencimento FROM devolucoes "
                        + "WHERE id_livro = ? AND matricula = ? ORDER BY ordem DESC LIMIT 1")) {
            ps.setInt(1, idLivro);
            ps.setInt(2, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Devolucao(
                            rs.getString("id"),
                            rs.getInt("id_livro"),
                            rs.getInt("matricula"),
                            LocalDate.parse(rs.getString("data")),
                            LocalDate.parse(rs.getString("data_vencimento"))));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar devolução do livro " + idLivro, e);
        }
    }

    private static int proximaOrdemDevolucao(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COALESCE(MAX(ordem), 0) + 1 FROM devolucoes");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        }
    }
}
