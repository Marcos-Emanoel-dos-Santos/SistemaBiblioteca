package com.ees.pp.interpretador;

public class Subtracao extends Operador
{
    public Subtracao(Expressao aEsquerda, Expressao aDireita)
    { super(aEsquerda, aDireita); }

    public char simboloOperacao()
    { return '-'; }

    public double calcular()
    { return aEsquerda.calcular() - aDireita.calcular(); }

    public String toString()
    { return "("+aEsquerda.toString()+ " - " + aDireita.toString() +")"; }
}