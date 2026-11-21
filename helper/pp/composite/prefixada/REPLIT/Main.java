package com.ees.pp.composite.prefixada.REPLIT;

import java.util.ArrayList;

abstract class Expressao
{
    abstract public double resolva();
    
    @Override
    abstract public String toString();
}

abstract class Operando extends Expressao
{
    protected double valor;

    public Operando(double valor)
    { this.valor = valor; }

    @Override
    public String toString()
    { return ""+this.valor; }

    @Override
    public double resolva()
    { return this.valor; }
}

class Constante extends Operando
{
    public Constante(double valor)
    {
        super( valor );
    }
}

class Variavel extends Operando
{
    private String nome;

    public Variavel(String nome, double valor)
    {
        super( valor );
        this.nome = nome;
    }
    public void atribuir(double valor)
    {
        this.valor = valor;
    }
    @Override
    public String toString()
    {
        return this.nome+":"+this.resolva();
    }
}

abstract class Operador extends Expressao {

    protected ArrayList<Expressao> elemento;

    public Operador(ArrayList<Expressao> elemento) {
        this.elemento = elemento;
    }

    abstract public char simbolo();

    @Override
    public String toString() {
        String s = "("+this.simbolo();
        for (Expressao e : this.elemento) {
            s = s + " "+e.toString();
        }
        return s+")";
    }
}

class Adicao extends Operador
{
    public Adicao(ArrayList<Expressao> elemento)
    { super(elemento); }

    @Override
    public char simbolo()
    { return '+'; }

    @Override
    public double resolva()
    { 
        double s = 0;
        for(Expressao e : this.elemento) {
            s = s + e.resolva();
        }
        return s; 
    }
}

class Subtracao extends Operador {

    public Subtracao(ArrayList<Expressao> elemento) {
        super(elemento);
    }

    @Override
    public char simbolo() {
        return '-';
    }

    @Override
    public double resolva(){
        double s = 0;
        boolean flag = true;
        for (Expressao e : this.elemento) {
            if (flag) {
                s = e.resolva();
                flag = false;
            } else {
                s = s - e.resolva();
            }
        }
        return s;
    }
}

class Divisao extends Operador
{
    public Divisao(ArrayList<Expressao> elemento)
    { super(elemento); }

    @Override
    public char simbolo()
    { return '/'; }

    @Override
    public double resolva()
    { 
        double s = 0;
        boolean flag = true;
        for (Expressao e : this.elemento) {
            if (flag) {
                s = e.resolva();
                flag = false;
            } else {
                s = s / e.resolva();
            }
        }
        return s; 
    }
}

class Multiplicacao extends Operador
{
    public Multiplicacao(ArrayList<Expressao> elemento)
    { super(elemento); }

    @Override
    public char simbolo()
    { return '*'; }

    @Override
    public double resolva()
    { 
        double s = 1;
        for(Expressao e : this.elemento) {
            s = s * e.resolva();
        }
        return s; 
    }
}

class Teste
{
    public static void execute()
    {
        ArrayList<Expressao> programa = new ArrayList();
        ArrayList<Expressao> la       = new ArrayList();
        ArrayList<Expressao> ls       = new ArrayList();
        ArrayList<Expressao> ld       = new ArrayList();
        ArrayList<Expressao> lm       = new ArrayList();

        Expressao c1 = new Constante(20.0); 
        programa.add(c1);
        
        Expressao c2 = new Constante(40.0); 
        programa.add(c2);
        
        Expressao v1 = new Variavel("v1", 10); 
        programa.add(v1);
        
        Expressao v2 = new Variavel("v2", 100); 
        programa.add(v2);
        
        la.add(c1); 
        la.add(v1);
        Expressao a = new Adicao(la); 
        programa.add(a);
        // la = (c1, v1);   a = (+ c1, v1)
        
        ls.add(a); 
        ls.add(v1);  
        Expressao s = new Subtracao(ls); 
        programa.add(s);
        // ls = (  (+ c1, v1), v1) 
        //  s = (- (+ c1, v1), v1)
        
        ld.add(s); 
        ld.add(a);    
        Expressao d = new Divisao(ld); 
        programa.add(d);
        // ld = (  (- (+ c1, v1), v1) (+ c1, v1))
        // d  = (/ (- (+ c1, v1), v1) (+ c1, v1))
        
        lm.add(a); 
        lm.add(s);
        Expressao m = new Multiplicacao(lm); 
        programa.add(m);
        // lm = (  (+ c1, v1), (- (+ c1, v1), v1))
        // m  = (* (+ c1, v1), (- (+ c1, v1), v1))
        
        // mostre a string de cada expressao (toString) e 
        // também o seu resultado (resolva).
	for ( Expressao p : programa)
            System.out.println(p.toString()+ " = " + p.resolva());
    }
    
}

public class Main {  
    
  public static void main(String args[]) { 
        Teste.execute();
  } 
  
}