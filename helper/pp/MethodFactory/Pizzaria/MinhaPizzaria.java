package com.ees.pp.MethodFactory.Pizzaria;

class MinhaPizzaria extends Pizzaria
{
    public Pizza createPizza(String tipo)
    {
        if (tipo.equals("Alho")) {
            return new PizzaDeAlho();
        } else if (tipo.equals("Cebola")) {
            return new PizzaDeCebola();
        } else {
            return new PizzaDeMilho();
        }
    }
}
