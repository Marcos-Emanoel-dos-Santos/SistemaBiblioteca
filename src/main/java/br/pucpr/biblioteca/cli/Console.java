package br.pucpr.biblioteca.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/** Utilitário de entrada e saída para a interface de linha de comando (sem regras de domínio). */
public final class Console {

    private final Scanner scanner;
    private final PrintStream saida;

    public Console(InputStream entrada, PrintStream saida) {
        this.scanner = new Scanner(entrada);
        this.saida = saida;
    }

    public void linha(String texto) {
        saida.println(texto);
    }

    public void linha() {
        saida.println();
    }

    public void titulo(String texto) {
        saida.println();
        saida.println("==================================================================");
        saida.println("  " + texto);
        saida.println("==================================================================");
    }

    public void secao(String texto) {
        saida.println();
        saida.println("--- " + texto + " ---");
    }

    public String lerTexto(String rotulo) {
        saida.print(rotulo + ": ");
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    public int lerInteiro(String rotulo) {
        while (true) {
            String valor = lerTexto(rotulo);
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                saida.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    public boolean lerSimNao(String rotulo) {
        while (true) {
            String valor = lerTexto(rotulo + " (s/n)").toLowerCase();
            if ("s".equals(valor) || "sim".equals(valor)) {
                return true;
            }
            if ("n".equals(valor) || "nao".equals(valor) || "não".equals(valor)) {
                return false;
            }
            saida.println("Responda 's' para sim ou 'n' para não.");
        }
    }
}
