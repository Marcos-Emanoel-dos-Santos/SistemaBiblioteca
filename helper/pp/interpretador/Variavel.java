package com.ees.pp.interpretador;

public class Variavel extends Operando
{
    private String nome;

    public Variavel(String nome, double valor)
    {
        super( valor );
        this.nome = nome;
    }
    public void atribuir(double valor)
    {
        this.valor = valor;
    }
    public String toString()
    {
        return this.nome;
    }
}
