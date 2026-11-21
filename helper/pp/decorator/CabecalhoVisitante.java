
package com.ees.pp.decorator;

public class CabecalhoVisitante extends Decorador
{
    public CabecalhoVisitante(Componente c) {
        super(c);
    }
    private void facaAlgo(){
        System.out.println("Olá, Pedro Silva!");
    }
    public void desenha() {
        this.facaAlgo();
        super.desenha();
    }
}