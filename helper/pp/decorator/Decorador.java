
package com.ees.pp.decorator;

abstract public class Decorador implements Componente
{
    Componente componente;

    //abstract public void desenha();

    public Decorador(Componente c) {
        componente = c;
    }

    public void desenha() {
        if(componente != null)
            componente.desenha();
    }
}