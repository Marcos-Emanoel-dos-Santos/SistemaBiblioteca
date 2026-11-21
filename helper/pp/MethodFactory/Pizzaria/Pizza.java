
package com.ees.pp.MethodFactory.Pizzaria;

abstract class Pizza {
    private double preco;

    public Pizza(double preco) {
        this.preco = preco;
    }

    public double getPreco() {
        return this.preco;
    }
}
