/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ees.pp.composite.REPLIT;

import java.util.ArrayList;

abstract class Expressao {

    abstract public double resolva();

    abstract public String toString();
}

abstract class Operando extends Expressao {

    protected double valor;

    public Operando(double valor) {
        this.valor = valor;
    }

    public String toString() {
        return "" + this.valor;
    }

    public double resolva() {
        return this.valor;
    }
}

class Constante extends Operando {

    public Constante(double valor) {
        super(valor);
    }
}

class Variavel extends Operando {

    private String nome;

    public Variavel(String nome, double valor) {
        super(valor);
        this.nome = nome;
    }

    public void atribua(double valor) {
        this.valor = valor;
    }

    public String toString() {
        return this.nome;
    }
}

abstract class Operador extends Expressao {

    protected Expressao aEsquerda, aDireita;

    public Operador(Expressao aEsquerda, Expressao aDireita) {
        this.aEsquerda = aEsquerda;
        this.aDireita = aDireita;
    }

    abstract public char simboloOperacao();

    public String toString() {
        return "(" + aEsquerda.toString() + " "+ this.simboloOperacao() + " " 
                   + aDireita.toString() + ")";
    }
}

class Adicao extends Operador {

    public Adicao(Expressao aEsquerda, Expressao aDireita) {
        super(aEsquerda, aDireita);
    }

    public char simboloOperacao() {
        return '+';
    }

    public double resolva() {
        return aEsquerda.resolva() + aDireita.resolva();
    }
}

class Subtracao extends Operador {

    public Subtracao(Expressao aEsquerda, Expressao aDireita) {
        super(aEsquerda, aDireita);
    }

    public char simboloOperacao() {
        return '-';
    }

    public double resolva() {
        return aEsquerda.resolva() - aDireita.resolva();
    }
}

class Multiplicacao extends Operador {

    public Multiplicacao(Expressao aEsquerda, Expressao aDireita) {
        super(aEsquerda, aDireita);
    }

    public char simboloOperacao() {
        return '*';
    }

    public double resolva() {
        return aEsquerda.resolva() * aDireita.resolva();
    }
}

class Divisao extends Operador {

    public Divisao(Expressao aEsquerda, Expressao aDireita) {
        super(aEsquerda, aDireita);
    }

    public char simboloOperacao() {
        return '/';
    }

    public double resolva() {
        return aEsquerda.resolva() / aDireita.resolva();
    }

}

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");

        ArrayList<Expressao> programa = new ArrayList<Expressao>();

        Expressao c1 = new Constante(20.0);
        programa.add(c1);
        Expressao c2 = new Constante(40.0);
        programa.add(c2);
        Expressao v1 = new Variavel("v1", 10);
        programa.add(v1);
        Expressao v2 = new Variavel("v2", 100);
        programa.add(v2);

        Expressao a = new Adicao(c1, v1);
        programa.add(a);
        Expressao s = new Subtracao(a, v1);
        programa.add(s);
        Expressao d = new Divisao(s, a);
        programa.add(d);
        Expressao m = new Multiplicacao(a, s);
        programa.add(m);

        for (Expressao p : programa) {
            System.out.println(p.toString() + " :- " + p.resolva());
        }
    }
}
