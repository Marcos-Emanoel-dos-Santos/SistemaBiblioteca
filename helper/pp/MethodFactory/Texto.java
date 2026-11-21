
package com.ees.pp.MethodFactory;

public abstract class Texto
{
    protected String conteudo;

    public Texto(String conteudo)
    {
        this.conteudo = conteudo;
    }
    public abstract void Exibir( );
}
