
package pp.Adapter;

public class ContaSimples implements Conta
{
    private int numero;
    private double saldo;

    public ContaSimples( int n, double s )
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
	return saldo;
    }
}
