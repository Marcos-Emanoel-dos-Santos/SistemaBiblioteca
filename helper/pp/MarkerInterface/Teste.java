package com.ees.pp.MarkerInterface;

import java.util.Vector;

public class Teste
{
    public static void execute()
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
            //if ( d instanceof Comprimivel)
            d.compacta();
        }
    }
}
