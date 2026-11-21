package br.pucpr.biblioteca.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * Parâmetros externos da instalação (lidos de {@code /biblioteca.properties} com sobrescrita
 * opcional via propriedades de sistema {@code -Dbiblioteca.*}).
 */
public record Configuracao(PerfilDeExecucao perfil,
                           String politicaDeMulta,
                           String bancoCobranca,
                           int diasValidadeReserva,
                           int diasPrazoEmprestimo,
                           int diasRenovacao,
                           boolean ativarCacheConsulta,
                           boolean ativarMedicaoConsulta,
                           String jdbcUrl,
                           String jdbcUsuario,
                           String jdbcSenha) {

    public static Configuracao padraoMemoria() {
        return new Configuracao(
                PerfilDeExecucao.MEMORIA,
                "ORIGINAL",
                "BANCO_A",
                3,
                14,
                30,
                true,
                true,
                "jdbc:mysql://localhost:3306/biblioteca",
                "biblioteca",
                "biblioteca");
    }

    public static Configuracao carregar() {
        Properties props = new Properties();
        try (InputStream in = Configuracao.class.getResourceAsStream("/biblioteca.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignorado) {
            // Usa valores padrão caso o arquivo não esteja presente
        }

        String perfilTexto = ler(props, "biblioteca.perfil", "MEMORIA").toUpperCase(Locale.ROOT);
        PerfilDeExecucao perfil = PerfilDeExecucao.valueOf(perfilTexto);

        return new Configuracao(
                perfil,
                ler(props, "biblioteca.multa.politica", "ORIGINAL").toUpperCase(Locale.ROOT),
                ler(props, "biblioteca.banco", "BANCO_A").toUpperCase(Locale.ROOT),
                Integer.parseInt(ler(props, "biblioteca.prazo.reserva.dias", "3")),
                Integer.parseInt(ler(props, "biblioteca.prazo.emprestimo.dias", "14")),
                Integer.parseInt(ler(props, "biblioteca.prazo.renovacao.dias", "30")),
                Boolean.parseBoolean(ler(props, "biblioteca.consulta.cache", "true")),
                Boolean.parseBoolean(ler(props, "biblioteca.consulta.medicao", "true")),
                ler(props, "biblioteca.jdbc.url", "jdbc:mysql://localhost:3306/biblioteca"),
                ler(props, "biblioteca.jdbc.usuario", "biblioteca"),
                ler(props, "biblioteca.jdbc.senha", "biblioteca"));
    }

    private static String ler(Properties props, String chave, String padrao) {
        String sys = System.getProperty(chave);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String valor = props.getProperty(chave);
        return (valor == null || valor.isBlank()) ? padrao : valor.trim();
    }
}
