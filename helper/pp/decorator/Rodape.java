
package com.ees.pp.decorator;

public class Rodape extends Decorador
{
    public Rodape(Componente c) {
        super(c);
    }

    private void facaAlgo(){
        System.out.println("SUBTOTOAL - XXXXXXXXXXXXXX");
    }

    public void desenha() {
        super.desenha();
        this.facaAlgo();
    }
}