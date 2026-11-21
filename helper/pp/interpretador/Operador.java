package com.ees.pp.interpretador;

abstract public class Operador extends Expressao
{
    protected Expressao aEsquerda, aDireita;

    public Operador(Expressao aEsquerda, Expressao aDireita)
    {
        this.aEsquerda = aEsquerda;
        this.aDireita = aDireita;
    }
    abstract public char simboloOperacao();
}
