/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ees.pp.MethodFactory;

public class MeuTexto extends Texto
{
    public MeuTexto(String conteudo)
    {
        super(conteudo);
    }

    @Override
    public void Exibir()
    {
        System.out.println( conteudo );
    }
}
