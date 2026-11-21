
package com.ees.pp.MethodFactory;

public abstract class EditorTexto {
	private Texto texto;

	public void CriarNovoTexto()
	{
		texto = NovoTexto();
		texto.Exibir();
	}

	abstract protected Texto NovoTexto( );
}
