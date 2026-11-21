package com.ees.pp.composite.binaria;

import java.util.ArrayList;

public class Teste {

    public static void execute() {
        ArrayList<Expressao> programa = new ArrayList();

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
            System.out.println(p.toString() + " :- " + p.calcular());
        }
    }

    public static void main(String args[]) {
        Teste.execute();
    }

}
