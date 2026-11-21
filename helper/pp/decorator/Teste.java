package com.ees.pp.decorator;

public class Teste
{
    public static void execute()
    {
        System.out.println("\nVisitante:");
        Componente cc = new Tela();
        cc = new CabecalhoVisitante( cc );
        cc.desenha();

        System.out.println("\nVisitante:");
        cc = new Tela();
        cc = new Rodape( cc );
        cc = new CabecalhoVisitanteLogado( cc );
        cc.desenha();

        System.out.println("\nAdministrador:");
        cc = new Tela();
        cc = new Rodape( cc );
        cc = new RodapeDetalhe( cc );
        cc = new CabecalhoAdministradorLogado( cc );
        cc = new CabecalhoVisitanteLogado( cc );
        cc.desenha();
    }
}
