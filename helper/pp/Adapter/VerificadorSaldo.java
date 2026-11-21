
package com.ees.pp.Adapter;

// VerificadorSaldo corresponde a Client

import java.util.Vector;

class VerificadorSaldo
{
    private Vector contas;

    public VerificadorSaldo( )
    {
	contas = new Vector( );
    }

    public void insiraConta( Conta c )
    {
	contas.add( c );
    }

    public void verifique( )
    {
	for ( int i = 0; i < contas.size( ); i++ )
	    {
		Conta c = (Conta) contas.elementAt( i );
		int numero = c.digaNumero( );
		double saldo = c.digaSaldo( );
		System.out.println( numero + " : " + saldo );
	    }
    }
}
