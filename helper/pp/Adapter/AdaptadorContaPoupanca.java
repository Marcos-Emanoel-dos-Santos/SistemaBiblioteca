
package com.ees.pp.Adapter;

// AdaptadorContaPoupanca a Adapter
class AdaptadorContaPoupanca implements Conta
{
    private ContaPoupanca cp;

    public AdaptadorContaPoupanca( ContaPoupanca cp )
    {
	this.cp = cp;
    }

    public int digaNumero( )
    {
	return cp.getNumero( );
    }

    public double digaSaldo( )
    {
	return cp.getSaldo( );
    }
}
