package com.ees.pp.composite.arquivo;

abstract class ComponenteSistemaArquivos {

    protected String nome;

    public ComponenteSistemaArquivos(String nome) {
        this.nome = nome;
    }

    abstract public void exibir_nome();

    abstract public void exibir_conteudo();

    public void listar_simples() {
        exibir_nome();
        exibir_conteudo();
    }

    abstract public void listar_recursivo(int nivel);

    protected void tabular(int k) {
        for (int i = 0; i < k; i++) {
            System.out.print("    ");
        }
    }
}
