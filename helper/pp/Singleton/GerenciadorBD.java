
package com.ees.pp.Singleton;

public class GerenciadorBD {
    static private GerenciadorBD TheInstance;
    private GerenciadorBD() {
        System.out.println("Construindo GerenciadorBD ...");
    }
    synchronized static public GerenciadorBD
            GetInstance() {
        if (TheInstance == null) {
            TheInstance = new GerenciadorBD();
        }
        return TheInstance;
    }
    public void AbrirConexao() {}
    public void FecharConexao() {}
    public void InserirRegistro() {}
}
