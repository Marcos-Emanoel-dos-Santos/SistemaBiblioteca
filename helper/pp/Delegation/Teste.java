/*
  Verifique as classes Pessoa e Tripulante e
  implemente as classes Bilheteiro e Passageiro
  analogamente a Tripulante.
*/

package com.ees.pp.Delegation;

public class Teste
{
    public static void execute()
    {
	Pessoa joao = new Pessoa( "Joao", 176 );
	Tripulante comandanteJoao = new Tripulante( joao, 70287 );
	comandanteJoao.imprimaAtributos( );
	comandanteJoao.mudeCodigoFuncional( 80910 );
	comandanteJoao.mudeAltura( 175 );
	comandanteJoao.imprimaAtributos( );

	Tripulante comissariaMaria = new Tripulante( "Maria", 165, 30208 );
	System.out.println( "Nome da comissaria = " + comissariaMaria.digaNome( ) );
	comissariaMaria.imprimaAtributos( );
	comissariaMaria.mudeAltura( 166 );
	comissariaMaria.imprimaAtributos( );

	Bilheteiro bilheteiroJoao =
	    new Bilheteiro( joao, 2873400 );
	bilheteiroJoao.imprimaAtributos( );
	bilheteiroJoao.mudeTelefone( 99347810 );
	bilheteiroJoao.mudeAltura( 180 );
	bilheteiroJoao.imprimaAtributos( );
	comandanteJoao.imprimaAtributos( );

	Passageiro passageiraMaria =
	    new Passageiro( comissariaMaria.digaPessoa( ), 34 );
	passageiraMaria.imprimaAtributos( );
	passageiraMaria.mudeAssento( 78 );
	passageiraMaria.mudeAltura( 170 );
	passageiraMaria.imprimaAtributos( );
	comissariaMaria.imprimaAtributos( );
    }
}
