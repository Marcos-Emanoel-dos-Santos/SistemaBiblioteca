
package com.ees.pp.composite.binaria;

public class Subtracao extends Operador
{
    public Subtracao(Expressao aEsquerda, Expressao aDireita)
    { super(aEsquerda, aDireita); }

    public char simboloOperacao()
    { return '-'; }

    public double calcular()
    { return aEsquerda.calcular() - aDireita.calcular(); }

}