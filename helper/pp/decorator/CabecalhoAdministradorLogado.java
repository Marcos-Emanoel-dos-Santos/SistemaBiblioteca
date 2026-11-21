
package com.ees.pp.decorator;

public class CabecalhoAdministradorLogado extends Decorador
{
    public CabecalhoAdministradorLogado(Componente c) {
        super(c);
    }
    private void facaAlgo(){
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA");
    }
    public void desenha() {
        this.facaAlgo();
        super.desenha();
    }
}
