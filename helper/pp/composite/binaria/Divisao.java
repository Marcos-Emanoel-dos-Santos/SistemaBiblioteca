
package com.ees.pp.composite.binaria;


public class Divisao extends Operador
{
    public Divisao(Expressao aEsquerda, Expressao aDireita)
    { super(aEsquerda, aDireita); }

    public char simboloOperacao()
    { return '/'; }

    public double calcular()
    { return aEsquerda.calcular() / aDireita.calcular(); }

}