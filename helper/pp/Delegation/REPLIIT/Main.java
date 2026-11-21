package com.ees.pp.Delegation.REPLIIT;

class Pessoa
{
    private String nome;
    private int altura; // em centimetros

    public Pessoa(String nome, int altura)
    { this.nome = nome; this.altura = altura; }

    public void mudeAltura( int altura )
    { this.altura = altura; }

    public String digaNome( ) { return nome; }
    public int digaAltura( ) { return altura; }

    public void imprimaAtributos( )
    {
	System.out.println( "ATRIBUTOS DE PESSOA:" );
	System.out.println( "  NOME   = " + nome );
	System.out.println( "  ALTURA = " + altura );
    }
}

class Bilheteiro
{
    private Pessoa aPessoa;

    private int telefone;

    // construtor de Bilheteiro que exige uma instancia de Pessoa

    public Bilheteiro( Pessoa p, int telefone )
    { aPessoa = p; this.telefone = telefone; }

    // construtor de Bilheteiro que constroi uma instancia de Pessoa

    public Bilheteiro( String nome, int altura, int telefone )
    { aPessoa = new Pessoa( nome, altura); this.telefone = telefone; }

    // metodos especificos de Bilheteiro

    public void mudeTelefone( int telefone )
    { this.telefone = telefone; }

    public int digaTelefone( ) { return telefone; }

    public Pessoa digaPessoa( ) { return aPessoa; }

    // metodos com delegaçao para a instancia de Pessoa

    public void mudeAltura( int altura )
    { aPessoa.mudeAltura( altura); }

    public String digaNome( ) { return aPessoa.digaNome( ); }
    public int digaAltura( ) { return aPessoa.digaAltura( ); }

    public void imprimaAtributos( )
    {
	System.out.println( );
	aPessoa.imprimaAtributos( ); // delegaçao
	System.out.println( "ATRIBUTOS DE TRIPULANTE:" );
	System.out.println( "  TELEFONE = " + telefone );
	System.out.println( );
    }
}

class Passageiro
{
    private Pessoa aPessoa;

    private int assento;

    // construtor de Passageiro que exige uma instancia de Pessoa

    public Passageiro( Pessoa p, int assento )
    { aPessoa = p; this.assento = assento; }

    // construtor de Passageiro que constroi uma instancia de Pessoa

    public Passageiro( String nome, int altura, int assento )
    { aPessoa = new Pessoa( nome, altura); this.assento = assento; }

    // metodos especificos de Passageiro

    public void mudeAssento( int assento )
    { this.assento = assento; }

    public int digaAssento( ) { return assento; }

    public Pessoa digaPessoa( ) { return aPessoa; }

    // metodos com delegaçao para a instancia de Pessoa

    public void mudeAltura( int altura )
    { aPessoa.mudeAltura( altura); }

    public String digaNome( ) { return aPessoa.digaNome( ); }
    public int digaAltura( ) { return aPessoa.digaAltura( ); }

    public void imprimaAtributos( )
    {
	System.out.println( );
	aPessoa.imprimaAtributos( ); // delegaçao
	System.out.println( "ATRIBUTOS DE TRIPULANTE:" );
	System.out.println( "  ASSENTO = " + assento );
	System.out.println( );
    }
}

class Tripulante
{
    private Pessoa aPessoa;

    private int codigoFuncional;

    // construtor de Tripulante que exige uma instancia de Pessoa

    public Tripulante( Pessoa p, int codigoFuncional )
    { aPessoa = p; this.codigoFuncional = codigoFuncional; }

    // construtor de Tripulante que constroi uma instancia de Pessoa

    public Tripulante( String nome, int altura, int codigoFuncional )
    { aPessoa = new Pessoa( nome, altura); this.codigoFuncional = codigoFuncional; }

    // metodos especificos de Tripulante

    public void mudeCodigoFuncional( int codigoFuncional )
    { this.codigoFuncional = codigoFuncional; }

    public int digaCodigoFuncional( ) { return codigoFuncional; }

    public Pessoa digaPessoa( ) { return aPessoa; }

    // metodos com delegaçao para a instancia de Pessoa

    public void mudeAltura( int altura )
    { aPessoa.mudeAltura( altura); }

    public String digaNome( ) { return aPessoa.digaNome( ); }
    public int digaAltura( ) { return aPessoa.digaAltura( ); }

    public void imprimaAtributos( )
    {
	System.out.println( );
	aPessoa.imprimaAtributos( ); // delegaçao
	System.out.println( "ATRIBUTOS DE TRIPULANTE:" );
	System.out.println( "  CODIGO = " + codigoFuncional );
	System.out.println( );
    }
}

public class Main {

  public static void execute(){

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

  public static void main(String[] args) {

    execute();
  }
}