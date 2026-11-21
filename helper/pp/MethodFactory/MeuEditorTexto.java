
package com.ees.pp.MethodFactory;

public class MeuEditorTexto extends EditorTexto
{
	@Override
	protected Texto NovoTexto()
        {
            return new MeuTexto("um novo texto foi criado");
	}

}
