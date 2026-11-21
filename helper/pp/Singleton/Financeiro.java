package com.ees.pp.Singleton;

public class Financeiro extends Thread {

    public void run() {
        GerenciadorBD gbd = GerenciadorBD.GetInstance();

        gbd.AbrirConexao();
        gbd.InserirRegistro();
        gbd.FecharConexao();

    }
}
