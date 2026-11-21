package br.pucpr.biblioteca.persistencia.jdbc;

import br.pucpr.biblioteca.persistencia.PersistenciaException;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * <b>R08 — Abstract Factory: produto concreto "unidade de trabalho" da família relacional (JDBC).</b>
 * <p>
 * Mantém uma única {@link Connection} compartilhada por todos os repositórios JDBC criados
 * com ela. A conexão é aberta de forma preguiçosa (apenas no primeiro uso), permitindo instanciar
 * e inspecionar a família de produção sem exigir que o servidor MySQL esteja ativo.
 */
public final class UnidadeDeTrabalhoJdbc implements UnidadeDeTrabalho, AutoCloseable {

    private final String url;
    private final String usuario;
    private final String senha;
    private Connection conexao;
    private boolean emAndamento;

    public UnidadeDeTrabalhoJdbc(String url, String usuario, String senha) {
        this.url = Objects.requireNonNull(url, "URL JDBC é obrigatória.");
        this.usuario = usuario;
        this.senha = senha;
    }

    /** Abre (se necessário) e devolve a conexão compartilhada por todos os repositórios desta família. */
    public Connection obterConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                if (usuario == null || usuario.isBlank()) {
                    conexao = DriverManager.getConnection(url);
                } else {
                    conexao = DriverManager.getConnection(url, usuario, senha == null ? "" : senha);
                }
            }
            return conexao;
        } catch (SQLException e) {
            throw new PersistenciaException("Não foi possível abrir conexão JDBC com: " + url, e);
        }
    }

    /** Inicializa as tabelas do {@code schema.sql} na conexão compartilhada. */
    public void inicializarEsquema() {
        InicializadorDeEsquema.inicializar(obterConexao());
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void iniciar() {
        if (emAndamento) {
            throw new IllegalStateException("Já existe uma transação JDBC em andamento.");
        }
        try {
            obterConexao().setAutoCommit(false);
            emAndamento = true;
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao iniciar transação JDBC.", e);
        }
    }

    @Override
    public void confirmar() {
        if (!emAndamento) {
            throw new IllegalStateException("Não há transação JDBC em andamento para confirmar.");
        }
        try {
            conexao.commit();
            conexao.setAutoCommit(true);
            emAndamento = false;
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao confirmar transação JDBC.", e);
        }
    }

    @Override
    public void desfazer() {
        if (!emAndamento) {
            return;
        }
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.rollback();
                conexao.setAutoCommit(true);
            }
            emAndamento = false;
        } catch (SQLException e) {
            emAndamento = false;
            throw new PersistenciaException("Falha ao desfazer transação JDBC.", e);
        }
    }

    @Override
    public boolean emAndamento() {
        return emAndamento;
    }

    @Override
    public void close() {
        if (conexao != null) {
            try {
                if (!conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                throw new PersistenciaException("Falha ao encerrar conexão JDBC.", e);
            } finally {
                conexao = null;
                emAndamento = false;
            }
        }
    }
}
