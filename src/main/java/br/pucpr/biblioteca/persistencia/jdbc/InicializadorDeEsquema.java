package br.pucpr.biblioteca.persistencia.jdbc;

import br.pucpr.biblioteca.persistencia.PersistenciaException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Carrega e executa {@code /schema.sql} sobre uma {@link Connection} JDBC.
 * Funciona de forma idempotente ({@code CREATE TABLE IF NOT EXISTS}) tanto em MySQL quanto em SQLite.
 */
public final class InicializadorDeEsquema {

    private static final String RECURSO_SCHEMA = "/schema.sql";

    private InicializadorDeEsquema() {
    }

    public static void inicializar(Connection conexao) {
        String script = lerScript();
        String[] comandos = script.split(";");
        try (Statement stmt = conexao.createStatement()) {
            for (String bruto : comandos) {
                String sql = removerComentarios(bruto).trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao inicializar esquema relacional.", e);
        }
    }

    private static String lerScript() {
        try (InputStream in = InicializadorDeEsquema.class.getResourceAsStream(RECURSO_SCHEMA)) {
            if (in == null) {
                throw new PersistenciaException("Arquivo de esquema não encontrado no classpath: " + RECURSO_SCHEMA, null);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String linha;
                while ((linha = reader.readLine()) != null) {
                    sb.append(linha).append('\n');
                }
                return sb.toString();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Falha ao ler arquivo " + RECURSO_SCHEMA, e);
        }
    }

    private static String removerComentarios(String trecho) {
        StringBuilder limpo = new StringBuilder();
        for (String linha : trecho.split("\\R")) {
            String trimmed = linha.trim();
            if (!trimmed.startsWith("--")) {
                limpo.append(linha).append('\n');
            }
        }
        return limpo.toString();
    }
}
