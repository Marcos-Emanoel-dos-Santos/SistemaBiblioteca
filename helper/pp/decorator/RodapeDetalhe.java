
package com.ees.pp.decorator;

public class RodapeDetalhe extends Decorador
{
    public RodapeDetalhe(Componente c) {
        super(c);
    }
    private void facaAlgo(){
        System.out.println("TOTAL     - ZZZZZZZZZZZZZZ");
    }
    public void desenha() {
        super.desenha();
        this.facaAlgo();
    }
}