package br.pucpr.biblioteca.persistencia.jdbc;

import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.persistencia.PersistenciaException;
import br.pucpr.biblioteca.persistencia.RepositorioDeLivros;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** <b>R08 — Abstract Factory: produto concreto "repositório de Livro" (família relacional JDBC).</b> */
public final class RepositorioDeLivrosJdbc implements RepositorioDeLivros {

    private final UnidadeDeTrabalhoJdbc unidadeDeTrabalho;

    public RepositorioDeLivrosJdbc(UnidadeDeTrabalhoJdbc unidadeDeTrabalho) {
        this.unidadeDeTrabalho = Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho JDBC é obrigatória.");
    }

    @Override
    public UnidadeDeTrabalho unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvar(Livro livro) {
        Objects.requireNonNull(livro, "Livro não pode ser nulo.");
        Connection conn = unidadeDeTrabalho.obterConexao();
        try {
            int atualizados;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE livros SET titulo = ?, estado = ? WHERE id_livro = ?")) {
                ps.setString(1, livro.getTitulo());
                ps.setString(2, livro.getEstado().nome());
                ps.setInt(3, livro.getIdLivro());
                atualizados = ps.executeUpdate();
            }
            if (atualizados == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO livros (id_livro, titulo, estado) VALUES (?, ?, ?)")) {
                    ps.setInt(1, livro.getIdLivro());
                    ps.setString(2, livro.getTitulo());
                    ps.setString(3, livro.getEstado().nome());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar livro " + livro.getIdLivro(), e);
        }
    }

    @Override
    public Optional<Livro> buscar(int idLivro) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_livro, titulo, estado FROM livros WHERE id_livro = ?")) {
            ps.setInt(1, idLivro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Livro.reconstituir(
                            rs.getInt("id_livro"),
                            rs.getString("titulo"),
                            rs.getString("estado")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar livro " + idLivro, e);
        }
    }

    @Override
    public void remover(int idLivro) {
        Connection conn = unidadeDeTrabalho.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM livros WHERE id_livro = ?")) {
            ps.setInt(1, idLivro);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover livro " + idLivro, e);
        }
    }

    @Override
    public List<Livro> listar() {
        Connection conn = unidadeDeTrabalho.obterConexao();
        List<Livro> resultado = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_livro, titulo, estado FROM livros ORDER BY id_livro");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(Livro.reconstituir(
                        rs.getInt("id_livro"),
                        rs.getString("titulo"),
                        rs.getString("estado")));
            }
            return List.copyOf(resultado);
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar livros.", e);
        }
    }
}
