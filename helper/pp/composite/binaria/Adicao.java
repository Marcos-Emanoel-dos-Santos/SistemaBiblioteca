
package com.ees.pp.composite.binaria;

public class Adicao extends Operador
{
    public Adicao(Expressao aEsquerda, Expressao aDireita)
    { super(aEsquerda, aDireita); }

    public char simboloOperacao()
    { return '+'; }

    public double calcular()
    { return aEsquerda.calcular() + aDireita.calcular(); }

}
