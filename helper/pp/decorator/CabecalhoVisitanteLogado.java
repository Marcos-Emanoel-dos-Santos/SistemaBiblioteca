
package com.ees.pp.decorator;

public class CabecalhoVisitanteLogado extends Decorador
{
    public CabecalhoVisitanteLogado(Componente c) {
        super(c);
    }
    private void facaAlgo(){
        System.out.println("[Logado]: Olá, Pedro Silva!");
    }
    public void desenha() {
        this.facaAlgo();
        super.desenha();
    }
}
