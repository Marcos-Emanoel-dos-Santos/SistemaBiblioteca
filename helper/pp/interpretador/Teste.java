package com.ees.pp.interpretador;

import java.util.ArrayList;

public class Teste
{
    public static void execute()
    {
        ArrayList<Expressao> programa = new ArrayList<Expressao>();

        Expressao c1 = new Constante(20.0); programa.add(c1);
        Expressao c2 = new Constante(40.0); programa.add(c2);
        Expressao v1 = new Variavel("v1", 10); programa.add(v1);
        Expressao v2 = new Variavel("v2", 100); programa.add(v2);

        Expressao a = new Adicao(c1,v1); programa.add(a);
        Expressao s = new Subtracao(a,v1); programa.add(s);
        Expressao d = new Divisao(s,a); programa.add(d);
        Expressao m = new Multiplicacao(a,s); programa.add(m);

	for ( Expressao p : programa)
            System.out.println(p.toString()+ " :- " + p.calcular());
    }

    public static void execute1()
    {
    	Expressao expr = null;

	// 6 + 4
	Constante c1  = new Constante( 6 );
	Constante c2  = new Constante( 4 );
	Adicao adicao   = new Adicao( c1, c2 );
	expr = adicao;

	System.out.println( expr.toString( ) + " = " + expr.calcular( ));

	// ( 5 - x ) * 2
	// x = 2
	Constante k = new Constante( 5 );
	Variavel var = new Variavel ( "x", 2 ); // nome e valor inicial
	Subtracao sub = new Subtracao( k, var );
	Multiplicacao mult = new Multiplicacao( sub, var );
	expr = mult;

	System.out.println( expr.toString( ) + " = " + expr.calcular( ) );

	// ( 5 - x ) * 2
	// x = 9
	var.atribuir( 9.0 ); // atualiza o valor da variável

	System.out.println( expr.toString( ) + " = " + expr.calcular( ) );

	// 9 - y * 4

	// ( a - b ) / ( a + b )
    }

}