
package com.ees.pp.Delegation;

public class Pessoa
{
    private String nome;
    private int altura; // em centimetros

    public Pessoa(String nome, int altura)
    { this.nome = nome; this.altura = altura; }

    public void mudeAltura( int altura )
    { this.altura = altura; }

    public String digaNome( ) { return nome; }
    public int digaAltura( ) { return altura; }

    public void imprimaAtributos( )
    {
	System.out.println( "ATRIBUTOS DE PESSOA:" );
	System.out.println( "  NOME   = " + nome );
	System.out.println( "  ALTURA = " + altura );
    }
}
