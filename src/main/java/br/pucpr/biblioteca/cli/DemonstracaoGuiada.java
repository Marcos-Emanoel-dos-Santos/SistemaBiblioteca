package br.pucpr.biblioteca.cli;

import br.pucpr.biblioteca.circulacao.solicitacao.SolicitacaoDeRenovacao;
import br.pucpr.biblioteca.circulacao.solicitacao.SolicitacaoDeReserva;
import br.pucpr.biblioteca.config.MontadorDoSistema;
import br.pucpr.biblioteca.config.RelogioAjustavel;
import br.pucpr.biblioteca.consulta.MetadadosLivro;
import br.pucpr.biblioteca.dominio.excecao.TransicaoInvalidaException;
import br.pucpr.biblioteca.dto.ComprovanteDeDevolucao;
import br.pucpr.biblioteca.dto.ComprovanteDeEmprestimo;
import br.pucpr.biblioteca.dto.ComprovanteDeMulta;
import br.pucpr.biblioteca.dto.ComprovanteDeRenovacao;
import br.pucpr.biblioteca.dto.ComprovanteDeReserva;
import br.pucpr.biblioteca.dto.DadosLivro;
import br.pucpr.biblioteca.financeiro.banco.BancoA;
import br.pucpr.biblioteca.financeiro.banco.BancoB;
import br.pucpr.biblioteca.organizacao.GrupoDoAcervo;
import br.pucpr.biblioteca.organizacao.TipoDeGrupo;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Roteiro executável do fluxo completo exigido na demonstração de 5 minutos:
 * cadastros (R07), hierarquia (R10), consulta decorada (R09), reserva e renovação idempotente
 * via Command (R06/R04/R01), empréstimo transacional (R04/R01), devolução atrasada com reações
 * independentes (R05), multa com Strategy (R02), cobrança e quitação idempotente via Adapter
 * do banco externo (R03) e inspeção final (R01).
 * <p>
 * Todas as operações de negócio da biblioteca passam exclusivamente pelos tratadores.
 */
public final class DemonstracaoGuiada {

    private final MontadorDoSistema.SistemaMontado sistema;
    private final RelogioAjustavel relogio;
    private final Console console;

    public DemonstracaoGuiada(MontadorDoSistema.SistemaMontado sistema,
                              RelogioAjustavel relogio,
                              Console console) {
        this.sistema = Objects.requireNonNull(sistema, "Sistema montado é obrigatório.");
        this.relogio = Objects.requireNonNull(relogio, "Relógio ajustável é obrigatório.");
        this.console = Objects.requireNonNull(console, "Console é obrigatório.");
    }

    public void executar() {
        LocalDate dataOriginal = LocalDate.now(relogio);
        try {
            executarFluxo(dataOriginal);
        } finally {
            relogio.definirData(dataOriginal);
        }
    }

    private void executarFluxo(LocalDate dataBase) {
        console.titulo("DEMONSTRACAO GUIADA — FLUXO COMPLETO DOS 10 PADROES GoF");
        console.linha("Perfil ativo (R08 Abstract Factory): " + sistema.configuracao().perfil()
                + " | Familia: " + sistema.fabricaDeDados().nomeDaFamilia());
        console.linha("Politica de multa (R02 Strategy):    "
                + sistema.configuracaoAdministrativa().getPoliticaVigente().nome());
        console.linha("Banco externo (R03 Adapter):         " + sistema.configuracao().bancoCobranca());
        console.linha("Data inicial simulada:               " + dataBase);

        int idLivroDemo = proximoIdLivroLivre();
        int matriculaDemo = 202601;
        int idAtendenteDemo = 901;
        String cpfEstudante = "111.222.333-44";
        String cpfAtendente = "222.333.444-55";

        // 1. Cadastros (R07 Template Method)
        console.secao("1. Cadastros via Tratadores (R07 — Template Method)");
        var pEst = sistema.tratadorDePessoa().inserirPessoa("Ana", "Silva", cpfEstudante);
        var pAt = sistema.tratadorDePessoa().inserirPessoa("Bruno", "Costa", cpfAtendente);
        var est = sistema.tratadorDeEstudante().inserirEstudante(cpfEstudante, matriculaDemo);
        var at = sistema.tratadorDeAtendente().inserirAtendente(cpfAtendente, idAtendenteDemo);
        var liv = sistema.tratadorDeAcervo().inserirLivro(idLivroDemo, "Padroes de Projeto — GoF");
        console.linha("Pessoa Estudante: " + pEst.mensagem());
        console.linha("Pessoa Atendente: " + pAt.mensagem());
        console.linha("Estudante:        " + est.mensagem());
        console.linha("Atendente:        " + at.mensagem());
        console.linha("Livro:            " + liv.mensagem());

        // 2. Organizacao hierarquica (R10 Composite) e Consulta publica (R09 Decorator)
        console.secao("2. Hierarquia (R10 — Composite) e Consulta Publica (R09 — Decorator)");
        GrupoDoAcervo unidade = new GrupoDoAcervo("Biblioteca Central", TipoDeGrupo.UNIDADE);
        GrupoDoAcervo secaoEng = new GrupoDoAcervo("Engenharia de Software", TipoDeGrupo.SECAO);
        sistema.tratadorDeAcervo().adicionarSubgrupo(unidade, secaoEng);
        sistema.tratadorDeAcervo().alocarLivroEmGrupo(secaoEng, idLivroDemo);
        console.linha("Contagem uniforme na Unidade (" + unidade.getNome() + "): "
                + unidade.contarLivros() + " livro(s) -> IDs " + unidade.listarIdsLivros());

        Optional<MetadadosLivro> c1 = sistema.tratadorDeAcervo().consultarMetadadosPublicos(idLivroDemo);
        Optional<MetadadosLivro> c2 = sistema.tratadorDeAcervo().consultarMetadadosPublicos(idLivroDemo);
        console.linha("1a consulta publica: " + c1.orElseThrow());
        console.linha("2a consulta publica: " + c2.orElseThrow());
        console.linha("Consultas na fonte original: " + sistema.consultaOriginal().getConsultasRealizadasNaFonte()
                + " | Acertos no cache: "
                + (sistema.consultaComCache() != null ? sistema.consultaComCache().getAcertosNoCache() : 0)
                + " | Medicoes registradas: " + sistema.registroDeMedicoes().quantidade());

        // 3. Reserva via Command (R06 + R04 + R01)
        console.secao("3. Reserva via SolicitacaoDeReserva (R06 Command + R04 Facade + R01 State)");
        SolicitacaoDeReserva solReserva = new SolicitacaoDeReserva(
                "DEMO-RES-" + idLivroDemo, "cli-demo", relogio.instant(), idLivroDemo, matriculaDemo);
        ComprovanteDeReserva compReserva = sistema.tratadorDeEmprestimo().executarSolicitacao(solReserva);
        console.linha("Comprovante de reserva: " + compReserva);
        console.linha("Estado atual do livro:  " + sistema.tratadorDeAcervo().consultarLivro(idLivroDemo).estado());

        // 4. Emprestimo transacional (R04 Facade + R01 State)
        console.secao("4. Efetivacao de Emprestimo (R04 Facade + R01 State)");
        ComprovanteDeEmprestimo compEmp = sistema.tratadorDeEmprestimo().emprestarLivro(idLivroDemo, matriculaDemo);
        console.linha("Comprovante de emprestimo: " + compEmp);
        console.linha("Estado atual do livro:     " + sistema.tratadorDeAcervo().consultarLivro(idLivroDemo).estado());

        // 5. Renovacao via Command idempotente (R06 + R01)
        console.secao("5. Renovacao Idempotente (R06 Command + R01 State)");
        SolicitacaoDeRenovacao solRenovacao = new SolicitacaoDeRenovacao(
                "DEMO-REN-" + idLivroDemo, "cli-demo", relogio.instant(), idLivroDemo, matriculaDemo);
        ComprovanteDeRenovacao ren1 = sistema.tratadorDeEmprestimo().executarSolicitacao(solRenovacao);
        ComprovanteDeRenovacao renRepetida = sistema.tratadorDeEmprestimo().executarSolicitacao(solRenovacao);
        console.linha("1a execucao (id=" + solRenovacao.getId() + "): " + ren1);
        console.linha("Reenvio com MESMO id (nao duplica +30d): " + renRepetida);

        // 6. Devolucao atrasada (+5 dias apos vencimento) -> R04 + R01 + R05 + R02 + R03
        console.secao("6. Devolucao Atrasada (R04 Facade + R05 Observer + R02 Strategy + R03 Adapter)");
        LocalDate dataDevolucaoAtrasada = ren1.novoVencimento().plusDays(5);
        relogio.definirData(dataDevolucaoAtrasada);
        console.linha("Relogio simulado avancado para " + dataDevolucaoAtrasada + " (5 dias apos o vencimento).");

        ComprovanteDeDevolucao compDev = sistema.tratadorDeDevolucao().registrarDevolucao(idLivroDemo, matriculaDemo);
        console.linha("Comprovante de devolucao: " + compDev);
        console.linha("Ouvintes acionados (R05):");
        console.linha("  - Inspecao pendente para livro " + idLivroDemo + "? "
                + sistema.agendaDeInspecao().contemLivroPendente(idLivroDemo));
        console.linha("  - Indicadores: total=" + sistema.painelDeIndicadores().getTotalDevolucoes()
                + ", atrasadas=" + sistema.painelDeIndicadores().getTotalAtrasadas()
                + ", somaDiasAtraso=" + sistema.painelDeIndicadores().getSomaDiasDeAtraso());
        console.linha("  - E-mails disparados: " + sistema.servicoDeEmail().listarEnviadas().size());

        ComprovanteDeMulta multa = sistema.tratadorDeDevolucao().emitirBoletoMulta(idLivroDemo, matriculaDemo);
        console.linha("Boleto de multa (f6 / 2a via): " + multa);

        // 7. Quitacao no Banco externo + idempotencia (R03 Adapter)
        console.secao("7. Pagamento no Banco Externo e Notificacao Repetida (R03 — Adapter)");
        boolean primeiraQuitacao;
        boolean segundaQuitacao;
        if ("BANCO_B".equals(sistema.configuracao().bancoCobranca())) {
            BancoB.AvisoBancoB aviso = sistema.bancoB()
                    .simularPagamento(multa.idCobranca(), dataDevolucaoAtrasada.plusDays(1));
            primeiraQuitacao = sistema.adaptadorBancoB().encaminharNotificacao(aviso, sistema.servicoDeMultas());
            segundaQuitacao = sistema.adaptadorBancoB().encaminharNotificacao(aviso, sistema.servicoDeMultas());
        } else {
            BancoA.NotificacaoBancoA notif = sistema.bancoA()
                    .simularPagamento(multa.idCobranca(), dataDevolucaoAtrasada.plusDays(1));
            primeiraQuitacao = sistema.adaptadorBancoA().encaminharNotificacao(notif, sistema.servicoDeMultas());
            segundaQuitacao = sistema.adaptadorBancoA().encaminharNotificacao(notif, sistema.servicoDeMultas());
        }
        ComprovanteDeMulta multaQuitada = sistema.tratadorDeDevolucao()
                .consultarMultaPorDevolucao(compDev.idDevolucao())
                .orElseThrow();
        console.linha("1a notificacao aceita? " + primeiraQuitacao
                + " | 2a notificacao repetida ignorada? " + (!segundaQuitacao));
        console.linha("Situacao final da multa (via TratadorDeDevolucao): " + multaQuitada.situacao()
                + " em " + multaQuitada.dataQuitacao());

        // 8. Bloqueio antes da inspecao e liberacao apos inspecao (R01 State)
        console.secao("8. Verificacao de Estado DEVOLVIDO e Inspecao Final (R01 — State)");
        try {
            sistema.tratadorDeEmprestimo().reservarLivro(idLivroDemo, matriculaDemo);
        } catch (TransicaoInvalidaException esperada) {
            console.linha("Reserva antes da inspecao recusada corretamente: " + esperada.getMessage());
        }
        DadosLivro aposInspecao = sistema.tratadorDeAcervo().inspecionarLivro(idLivroDemo, true);
        console.linha("Apos inspecao aprovada: livro " + aposInspecao.idLivro()
                + " -> estado " + aposInspecao.estado());
        console.titulo("FIM DA DEMONSTRACAO GUIADA");
    }

    private int proximoIdLivroLivre() {
        int candidato = 9001;
        for (DadosLivro livro : sistema.tratadorDeAcervo().listarLivros()) {
            if (livro.idLivro() >= candidato) {
                candidato = livro.idLivro() + 1;
            }
        }
        return candidato;
    }
}
