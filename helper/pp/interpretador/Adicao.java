
package com.ees.pp.interpretador;

public class Adicao extends Operador
{
    public Adicao(Expressao aEsquerda, Expressao aDireita)
    { super(aEsquerda, aDireita); }

    public char simboloOperacao()
    { return '+'; }

    public double calcular()
    { return aEsquerda.calcular() + aDireita.calcular(); }

    public String toString()
    { return "("+aEsquerda.toString()+" + " + aDireita.toString() +")"; }
}
