package br.pucpr.biblioteca;

import br.pucpr.biblioteca.cli.Console;
import br.pucpr.biblioteca.cli.DemonstracaoGuiada;
import br.pucpr.biblioteca.cli.MenuPrincipal;
import br.pucpr.biblioteca.config.Configuracao;
import br.pucpr.biblioteca.config.MontadorDoSistema;
import br.pucpr.biblioteca.config.PerfilDeExecucao;
import br.pucpr.biblioteca.config.RelogioAjustavel;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.time.LocalDate;

/**
 * Ponto de entrada da aplicação CLI do Sistema de Gestão de Biblioteca.
 * <p>
 * Uso:
 * <ul>
 *   <li>{@code mvn compile exec:java} — inicia o menu interativo no perfil configurado;</li>
 *   <li>{@code mvn compile exec:java -Dexec.args="--demo"} — executa diretamente a demonstração guiada;</li>
 *   <li>{@code mvn compile exec:java -Dbiblioteca.perfil=PRODUCAO} — executa usando a família relacional MySQL.</li>
 * </ul>
 */
public final class Aplicacao {

    private Aplicacao() {
    }

    public static void main(String[] args) {
        Configuracao configuracao = Configuracao.carregar();
        RelogioAjustavel relogio = RelogioAjustavel.em(LocalDate.now());
        boolean inicializarEsquemaJdbc = configuracao.perfil() == PerfilDeExecucao.PRODUCAO;

        MontadorDoSistema.SistemaMontado sistema = MontadorDoSistema.montar(
                configuracao, relogio, inicializarEsquemaJdbc);
        Console console = new Console(System.in, System.out);

        try {
            DemonstracaoGuiada demo = new DemonstracaoGuiada(sistema, relogio, console);
            if (contemArgumento(args, "--demo")) {
                demo.executar();
                return;
            }

            String resumo = "Perfil: " + configuracao.perfil()
                    + " (" + sistema.fabricaDeDados().nomeDaFamilia() + ")"
                    + " | Multa: " + configuracao.politicaDeMulta()
                    + " | Banco: " + configuracao.bancoCobranca();

            MenuPrincipal menu = new MenuPrincipal(
                    sistema.tratadorDePessoa(),
                    sistema.tratadorDeEstudante(),
                    sistema.tratadorDeAtendente(),
                    sistema.tratadorDeAcervo(),
                    sistema.tratadorDeEmprestimo(),
                    sistema.tratadorDeDevolucao(),
                    demo,
                    relogio,
                    console,
                    resumo);
            menu.iniciar();
        } finally {
            encerrarRecursos(sistema.acessoADados().unidadeDeTrabalho());
        }
    }

    private static boolean contemArgumento(String[] args, String alvo) {
        if (args == null) {
            return false;
        }
        for (String arg : args) {
            if (alvo.equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }

    private static void encerrarRecursos(UnidadeDeTrabalho unidadeDeTrabalho) {
        if (unidadeDeTrabalho instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception ignorado) {
                // Encerramento seguro da conexão ao finalizar a CLI
            }
        }
    }
}
