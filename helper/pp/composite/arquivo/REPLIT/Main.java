package com.ees.pp.composite.arquivo.REPLIT;

import java.util.Vector;

abstract class Elemento {

    protected String nome;

    public Elemento(String nome) {
        this.nome = nome;
    }

    abstract public void exibir_nome();

    public void listar() {
        exibir_nome();
    }

    abstract public void listar(int nivel);

    protected void tabular(int k) {
        for (int i = 0; i < k; i++) {
            System.out.print("    ");
        }
    }
}

class Arquivo extends Elemento {

    private String texto;

    public Arquivo(String nome, String texto) {
        super(nome);
        this.texto = texto;
    }

    @Override
    public void exibir_nome() {
        System.out.println("ARQUIVO : " + nome + ";  CONEUDO : " + texto);
    }

    @Override
    public void listar(int nivel) {
        tabular(nivel);
        exibir_nome();
    }
}

class Pasta extends Elemento {

    private Vector<Elemento> componentes;

    public Pasta(String nome) {
        super(nome);
        componentes = new Vector();
    }

    public void add(Elemento c) {
        componentes.add(c);
    }

    @Override
    public void exibir_nome() {
        System.out.println("PASTA   : " + nome);
    }

    @Override
    public void listar(int nivel) {
        tabular(nivel);
        exibir_nome();
        for (int i = 0; i < componentes.size(); i++) {
            componentes.elementAt(i).listar(nivel + 1);
        }
    }
}

public class Main {

    public static void main(String[] args) {

        Pasta p1 = new Pasta("p1");

        Arquivo a1 = new Arquivo("a1", "1111111111");
        p1.add(a1);

        Pasta p2 = new Pasta("p2");
        p1.add(p2);

        System.out.println("---- p1.listar_simples ----");
        p1.listar();

        System.out.println("---- p1.listar_recursivo ----");
        p1.listar(0);

        System.out.println("---- a1.listar_simples ----");
        a1.listar();

        System.out.println("---- p2.listar_simples ----");
        p2.listar();
    }
}
