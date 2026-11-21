
package com.ees.pp.Delegation;

public class Passageiro
{
    private Pessoa aPessoa;

    private int assento;

    // construtor de Passageiro que exige uma instancia de Pessoa

    public Passageiro( Pessoa p, int assento )
    { aPessoa = p; this.assento = assento; }

    // construtor de Passageiro que constroi uma instancia de Pessoa

    public Passageiro( String nome, int altura, int assento )
    { aPessoa = new Pessoa( nome, altura); this.assento = assento; }

    // metodos especificos de Passageiro

    public void mudeAssento( int assento )
    { this.assento = assento; }

    public int digaAssento( ) { return assento; }

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
	System.out.println( "  ASSENTO = " + assento );
	System.out.println( );
    }
}
