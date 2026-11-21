package com.ees.pp.composite.arquivo;

import java.util.Vector;

class Pasta extends ComponenteSistemaArquivos {

    private Vector<ComponenteSistemaArquivos> componentes;

    public Pasta(String nome) {
        super(nome);
        componentes = new Vector();
    }

    public void add(ComponenteSistemaArquivos c) {
        componentes.add(c);
    }

    @Override
    public void exibir_nome() {
        System.out.println("PASTA   : " + nome);
    }

    @Override
    public void exibir_conteudo() {
        for (int i = 0; i < componentes.size(); i++) {
            componentes.elementAt(i).exibir_nome();
        }
    }

    @Override
    public void listar_recursivo(int nivel) {
        tabular(nivel);
        exibir_nome();
        for (int i = 0; i < componentes.size(); i++) {
            componentes.elementAt(i).listar_recursivo(nivel + 1);
        }
    }
}
