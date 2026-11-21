/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ees.pp.MarkerInterface.REPLIT;

import java.util.Vector;

interface Confidencial { }

interface Imprimivel { }

abstract class Documento {
    private String texto;

    public Documento(String texto) {
        this.texto = texto;
    }
    public void imprima() {
        if (this instanceof Imprimivel) {
            System.out.println(texto);
        }
    }
    public void criptografe() // simplesmente, inverte os caracteres
    {
        if (this instanceof Confidencial) {
            StringBuffer sb = new StringBuffer(texto);
            StringBuffer invertida = sb.reverse();
            texto = invertida.toString();
        }
    }
}


class Projeto extends Documento
        implements Confidencial
{
	public Projeto( String texto )
	{
		super( texto );
	}
}


class Relatorio extends Documento
        implements Imprimivel
{
	public Relatorio( String texto )
	{
		super( texto );
	}
}

class Circular extends Documento
        implements Imprimivel, Confidencial
{
	public Circular( String texto )
	{
		super( texto );
	}
}


public class Main {
    
    public static void main(String [] args)
    {
        Vector<Documento> colecao = new Vector<Documento> ( );

        colecao.add( new Projeto( "Projeto abc ..." ) );
        colecao.add( new Relatorio( "Relatorio xyz ..." ) );
        colecao.add( new Circular( "Circular rst ...") );

        for ( Documento d: colecao)
        {
            //if ( d instanceof Imprimivel)
            d.imprima();
            //if ( d instanceof Confidencial)
            d.criptografe();
            //if ( d instanceof Imprimivel)
            d.imprima();
        }
    }
}
