
package com.ees.pp.composite.binaria;

abstract public class Operador extends Expressao
{
    protected Expressao aEsquerda, aDireita;

    public Operador(Expressao aEsquerda, Expressao aDireita)
    {
        this.aEsquerda = aEsquerda;
        this.aDireita = aDireita;
    }
    abstract public char simboloOperacao();
    
    @Override
    public String toString()
    { return "("+aEsquerda.toString()+ " " + this.simboloOperacao() + " " + aDireita.toString() +")"; }

}
