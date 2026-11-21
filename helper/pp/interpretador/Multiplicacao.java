package com.ees.pp.interpretador;

public class Multiplicacao extends Operador
{
    public Multiplicacao(Expressao aEsquerda, Expressao aDireita)
    { super(aEsquerda, aDireita); }

    public char simboloOperacao()
    { return '*'; }

    public double calcular()
    { return aEsquerda.calcular() * aDireita.calcular(); }

    public String toString()
    { return "("+aEsquerda.toString()+" * " + aDireita.toString() +")"; }
}

