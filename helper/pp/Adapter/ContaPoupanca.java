
package com.ees.pp.Adapter;

// ContaPoupanca corresponde a Adaptee
class ContaPoupanca
{
    private int numero;
    private float saldo;

    public ContaPoupanca( int n, float s )
    {
	numero = n;
	saldo = s;
    }

    public int getNumero( )
    {
	return numero;
    }

    public float getSaldo( )
    {
	return saldo * 0.9F; // retem 10% do saldo
    }
}
