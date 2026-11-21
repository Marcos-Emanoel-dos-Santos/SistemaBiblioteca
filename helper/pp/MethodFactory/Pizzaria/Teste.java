package com.ees.pp.MethodFactory.Pizzaria;

import java.util.ArrayList;

public class Teste
{
    static public void execute() {
        ArrayList<Pizza> venda = new ArrayList<Pizza>();

        MinhaPizzaria factory = new MinhaPizzaria();

        venda.add(factory.createPizza("Default"));
        venda.add(factory.createPizza("Alho"));
        venda.add(factory.createPizza("Cebola"));
        venda.add(factory.createPizza("Milho"));

        double faturamento = 0;
        for (Pizza p : venda) {
            faturamento = faturamento + p.getPreco();
            System.out.println(p.toString()+ " : "+ p.getPreco());
        }
        System.out.println("Faturamento de " + faturamento);
    }
}
