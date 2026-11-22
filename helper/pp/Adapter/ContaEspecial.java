
package pp.Adapter;

class ContaEspecial implements Conta
{
    private int numero;
    private double saldo;

    public ContaEspecial( int n, double s )
    {
	numero = n;
	saldo = s;
    }

    public int digaNumero( )
    {
	return numero;
    }

    public double digaSaldo( )
    {
	return saldo * 1.1; // saldo com tolerancia
    }
}
