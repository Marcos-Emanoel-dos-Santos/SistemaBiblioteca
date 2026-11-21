package com.ees.pp.composite.arquivo;

class Arquivo extends ComponenteSistemaArquivos {

    private String texto;

    public Arquivo(String nome, String texto) {
        super(nome);
        this.texto = texto;
    }

    @Override
    public void exibir_nome() {
        System.out.println("ARQUIVO : " + nome);
    }

    @Override
    public void exibir_conteudo() {
        System.out.println("CONTEUDO: " + texto);
    }

    @Override
    public void listar_recursivo(int nivel) {
        tabular(nivel);
        exibir_nome();
    }
}
