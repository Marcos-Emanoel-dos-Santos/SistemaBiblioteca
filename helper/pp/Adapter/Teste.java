
package com.ees.pp.Adapter;

// classe de teste do pattern
public class Teste
{
    public static void execute()
    {
	VerificadorSaldo verificador = new VerificadorSaldo( );

	ContaSimples c1 = new ContaSimples( 111, 10.0 );
	ContaSimples c2 = new ContaSimples( 222, 20.0 );
	ContaSimples c3 = new ContaSimples( 333, 30.0 );

	verificador.insiraConta( c1 );
	verificador.insiraConta( c2 );
	verificador.insiraConta( c3 );

	ContaEspecial c4 = new ContaEspecial( 444, 40.0 );
	ContaEspecial c5 = new ContaEspecial( 555, 50.0 );
	ContaEspecial c6 = new ContaEspecial( 666, 60.0 );

	verificador.insiraConta( c4 );
	verificador.insiraConta( c5 );
	verificador.insiraConta( c6 );

	ContaPoupanca c7 = new ContaPoupanca( 777, 70.0F );
	ContaPoupanca c8 = new ContaPoupanca( 888, 80.0F );
	ContaPoupanca c9 = new ContaPoupanca( 999, 90.0F );

	AdaptadorContaPoupanca a1 = new AdaptadorContaPoupanca( c7 );
	AdaptadorContaPoupanca a2 = new AdaptadorContaPoupanca( c8 );
	AdaptadorContaPoupanca a3 = new AdaptadorContaPoupanca( c9 );

	verificador.insiraConta( a1 );
	verificador.insiraConta( a2 );
	verificador.insiraConta( a3 );

	verificador.verifique( );
    }
}
