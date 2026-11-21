
package com.ees.pp.Delegation;

class Bilheteiro
{
    private Pessoa aPessoa;

    private int telefone;

    // construtor de Bilheteiro que exige uma instancia de Pessoa

    public Bilheteiro( Pessoa p, int telefone )
    { aPessoa = p; this.telefone = telefone; }

    // construtor de Bilheteiro que constroi uma instancia de Pessoa

    public Bilheteiro( String nome, int altura, int telefone )
    { aPessoa = new Pessoa( nome, altura); this.telefone = telefone; }

    // metodos especificos de Bilheteiro

    public void mudeTelefone( int telefone )
    { this.telefone = telefone; }

    public int digaTelefone( ) { return telefone; }

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
	System.out.println( "  TELEFONE = " + telefone );
	System.out.println( );
    }
}
