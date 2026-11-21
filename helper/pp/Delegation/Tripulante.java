
package com.ees.pp.Delegation;

public class Tripulante
{
    private Pessoa aPessoa;

    private int codigoFuncional;

    // construtor de Tripulante que exige uma instancia de Pessoa

    public Tripulante( Pessoa p, int codigoFuncional )
    { aPessoa = p; this.codigoFuncional = codigoFuncional; }

    // construtor de Tripulante que constroi uma instancia de Pessoa

    public Tripulante( String nome, int altura, int codigoFuncional )
    { aPessoa = new Pessoa( nome, altura); this.codigoFuncional = codigoFuncional; }

    // metodos especificos de Tripulante

    public void mudeCodigoFuncional( int codigoFuncional )
    { this.codigoFuncional = codigoFuncional; }

    public int digaCodigoFuncional( ) { return codigoFuncional; }

    public Pessoa digaPessoa( ) { return aPessoa; }

    // metodos com delegaçao para a instancia de Pessoa

    public void mudeAltura( int altura )
    { aPessoa.mudeAltura( altura); }

    public String digaNome( ) { return aPessoa.digaNome( ); }
    public int digaAltura( ) { return aPessoa.digaAltura( ); }

    public void imprimaAtributos( )
    {
	System.out.println( );
	aPessoa.imprimaAtributos( ); // delegaçao
	System.out.println( "ATRIBUTOS DE TRIPULANTE:" );
	System.out.println( "  CODIGO = " + codigoFuncional );
	System.out.println( );
    }
}
