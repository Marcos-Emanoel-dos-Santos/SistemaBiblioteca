
package com.ees.pp.Singleton;

public class Teste
{
	public static void execute()
        {
		Secretaria s = new Secretaria( );
		Financeiro f = new Financeiro( );

		s.start( );
		f.start( );

	}
}
