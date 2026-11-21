
package com.ees.pp.composite.arquivo;

public class Teste
{
    public static void execute()
    {
	Pasta p1 = new Pasta( "p1" );

	Arquivo a1 = new Arquivo( "a1", "1111111111" );
	p1.add( a1 );

	Pasta p2 = new Pasta ( "p2" );
	p1.add( p2 );

	System.out.println( "---- p1.listar_simples ----" );
	p1.listar_simples( );

	System.out.println( "---- p1.listar_recursivo ----" );
	p1.listar_recursivo( 0 );

	System.out.println( "---- a1.listar_simples ----" );
	a1.listar_simples( );

	System.out.println( "---- p2.listar_simples ----" );
	p2.listar_simples( );
    }
}
