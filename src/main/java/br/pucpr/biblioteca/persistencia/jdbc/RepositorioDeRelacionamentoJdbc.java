package br.pucpr.biblioteca.persistencia.jdbc;

import br.pucpr.biblioteca.dominio.pessoa.Atendente;
import br.pucpr.biblioteca.dominio.pessoa.Estudante;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.persistencia.PersistenciaException;
import br.pucpr.biblioteca.persistencia.RepositorioDeRelacionamento;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <b>R08 — Abstract Factory: produto concreto "repositório de Relacionamento" (família relacional JDBC).</b>
 */
public final class RepositorioDeRelacionamentoJdbc implements RepositorioDeRelacionamento {

    private final UnidadeDeTrabalhoJdbc unidadeDeTrabalho;

    public RepositorioDeRelacionamentoJdbc(UnidadeDeTrabalhoJdbc unidadeDeTrabalho) {
        this.unidadeDeTrabalho = Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho JDBC é obrigatória.");
    }

    @Override
    public UnidadeDeTrabalho unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvarPessoa(Pessoa pessoa) {
        Objects.requireNonNull(pessoa, "Pessoa não pode ser nula.");
        Connection conn = unidadeDeTrabalho.obterConexao();
        try {
            int atualizados;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE pessoas SET nome = ?, sobrenome = ? WHERE cpf = ?")) {
                ps.setString(1, pessoa.getNome());
                ps.setString(2, pessoa.getSobrenome());
                ps.setString(3, pessoa.getCpf());
                atualizados = ps.executeUpdate();
            }
            if (atualizados == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO pessoas (cpf, nome, sobrenome) VALUES (?, ?, ?)")) {
                    ps.setString(1, pessoa.getCpf());
                    ps.setString(2, pessoa.getNome());
                    ps.setString(3, pessoa.getSobrenome());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar pessoa " + pessoa.getCpf(), e);
        }
    }

    @Override
    public Optional<Pessoa> buscarPessoa(String cpf) {
        if (cpf == null) {
            return Optional.empty();
        }
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT cpf, nome, sobrenome FROM pessoas WHERE cpf = ?")) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Pessoa(
                            rs.getString("nome"),
                            rs.getString("sobrenome"),
                            rs.getString("cpf")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar pessoa " + cpf, e);
        }
    }

    @Override
    public void removerPessoa(String cpf) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM pessoas WHERE cpf = ?")) {
            ps.setString(1, cpf);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover pessoa " + cpf, e);
        }
    }

    @Override
    public List<Pessoa> listarPessoas() {
        Connection conn = unidadeDeTrabalho.obterConexao();
        List<Pessoa> resultado = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT cpf, nome, sobrenome FROM pessoas ORDER BY cpf");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new Pessoa(
                        rs.getString("nome"),
                        rs.getString("sobrenome"),
                        rs.getString("cpf")));
            }
            return List.copyOf(resultado);
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar pessoas.", e);
        }
    }

    @Override
    public void salvarEstudante(Estudante estudante) {
        Objects.requireNonNull(estudante, "Estudante não pode ser nulo.");
        Connection conn = unidadeDeTrabalho.obterConexao();
        try {
            int atualizados;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE estudantes SET cpf = ? WHERE matricula = ?")) {
                ps.setString(1, estudante.getCpf());
                ps.setInt(2, estudante.getMatricula());
                atualizados = ps.executeUpdate();
            }
            if (atualizados == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO estudantes (matricula, cpf) VALUES (?, ?)")) {
                    ps.setInt(1, estudante.getMatricula());
                    ps.setString(2, estudante.getCpf());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar estudante " + estudante.getMatricula(), e);
        }
    }

    @Override
    public Optional<Estudante> buscarEstudante(int matricula) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT matricula, cpf FROM estudantes WHERE matricula = ?")) {
            ps.setInt(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Estudante(
                            rs.getString("cpf"),
                            rs.getInt("matricula")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar estudante " + matricula, e);
        }
    }

    @Override
    public void removerEstudante(int matricula) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM estudantes WHERE matricula = ?")) {
            ps.setInt(1, matricula);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover estudante " + matricula, e);
        }
    }

    @Override
    public List<Estudante> listarEstudantes() {
        Connection conn = unidadeDeTrabalho.obterConexao();
        List<Estudante> resultado = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT matricula, cpf FROM estudantes ORDER BY matricula");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new Estudante(
                        rs.getString("cpf"),
                        rs.getInt("matricula")));
            }
            return List.copyOf(resultado);
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar estudantes.", e);
        }
    }

    @Override
    public void salvarAtendente(Atendente atendente) {
        Objects.requireNonNull(atendente, "Atendente não pode ser nulo.");
        Connection conn = unidadeDeTrabalho.obterConexao();
        try {
            int atualizados;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE atendentes SET cpf = ? WHERE id_atendente = ?")) {
                ps.setString(1, atendente.getCpf());
                ps.setInt(2, atendente.getIdAtendente());
                atualizados = ps.executeUpdate();
            }
            if (atualizados == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO atendentes (id_atendente, cpf) VALUES (?, ?)")) {
                    ps.setInt(1, atendente.getIdAtendente());
                    ps.setString(2, atendente.getCpf());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar atendente " + atendente.getIdAtendente(), e);
        }
    }

    @Override
    public Optional<Atendente> buscarAtendente(int idAtendente) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_atendente, cpf FROM atendentes WHERE id_atendente = ?")) {
            ps.setInt(1, idAtendente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Atendente(
                            rs.getString("cpf"),
                            rs.getInt("id_atendente")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar atendente " + idAtendente, e);
        }
    }

    @Override
    public void removerAtendente(int idAtendente) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM atendentes WHERE id_atendente = ?")) {
            ps.setInt(1, idAtendente);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover atendente " + idAtendente, e);
        }
    }

    @Override
    public List<Atendente> listarAtendentes() {
        Connection conn = unidadeDeTrabalho.obterConexao();
        List<Atendente> resultado = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_atendente, cpf FROM atendentes ORDER BY id_atendente");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new Atendente(
                        rs.getString("cpf"),
                        rs.getInt("id_atendente")));
            }
            return List.copyOf(resultado);
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar atendentes.", e);
        }
    }
}
