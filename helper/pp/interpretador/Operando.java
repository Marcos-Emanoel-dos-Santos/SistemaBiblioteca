package com.ees.pp.interpretador;

abstract public class Operando extends Expressao
{
    protected double valor;

    public Operando(double valor)
    { this.valor = valor; }

    public String toString()
    { return ""+this.valor; }

    public double calcular()
    { return this.valor; }
}
