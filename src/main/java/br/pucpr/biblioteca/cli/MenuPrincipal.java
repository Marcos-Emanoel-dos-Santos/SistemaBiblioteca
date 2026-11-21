package br.pucpr.biblioteca.cli;

import br.pucpr.biblioteca.circulacao.solicitacao.SolicitacaoDeRenovacao;
import br.pucpr.biblioteca.circulacao.solicitacao.SolicitacaoDeReserva;
import br.pucpr.biblioteca.consulta.MetadadosLivro;
import br.pucpr.biblioteca.dominio.excecao.RegraDeNegocioException;
import br.pucpr.biblioteca.dto.ComprovanteDeDevolucao;
import br.pucpr.biblioteca.dto.ComprovanteDeEmprestimo;
import br.pucpr.biblioteca.dto.ComprovanteDeMulta;
import br.pucpr.biblioteca.dto.ComprovanteDeRenovacao;
import br.pucpr.biblioteca.dto.ComprovanteDeReserva;
import br.pucpr.biblioteca.dto.DadosAtendente;
import br.pucpr.biblioteca.dto.DadosEstudante;
import br.pucpr.biblioteca.dto.DadosLivro;
import br.pucpr.biblioteca.dto.DadosPessoa;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;
import br.pucpr.biblioteca.interfaces.iAtendente;
import br.pucpr.biblioteca.interfaces.iDevolucao;
import br.pucpr.biblioteca.interfaces.iEmprestimo;
import br.pucpr.biblioteca.interfaces.iEstudante;
import br.pucpr.biblioteca.interfaces.iPessoa;
import br.pucpr.biblioteca.organizacao.GrupoDoAcervo;
import br.pucpr.biblioteca.organizacao.TipoDeGrupo;
import br.pucpr.biblioteca.persistencia.PersistenciaException;
import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface de linha de comando (CLI) do Sistema de Gestão de Biblioteca.
 * <p>
 * Depende <b>exclusivamente dos tratadores</b> e DTOs — não contém regras de domínio nem acessa
 * {@code AcervoDeLivros}, {@code Relacionamento}, {@code Movimentacao} ou repositórios diretamente.
 */
public final class MenuPrincipal {

    private final iPessoa tratadorDePessoa;
    private final iEstudante tratadorDeEstudante;
    private final iAtendente tratadorDeAtendente;
    private final TratadorDeAcervo tratadorDeAcervo;
    private final iEmprestimo tratadorDeEmprestimo;
    private final iDevolucao tratadorDeDevolucao;
    private final DemonstracaoGuiada demonstracaoGuiada;
    private final Clock relogio;
    private final Console console;
    private final String resumoAmbiente;

    private final Map<String, GrupoDoAcervo> gruposCadastrados = new LinkedHashMap<>();

    public MenuPrincipal(iPessoa tratadorDePessoa,
                         iEstudante tratadorDeEstudante,
                         iAtendente tratadorDeAtendente,
                         TratadorDeAcervo tratadorDeAcervo,
                         iEmprestimo tratadorDeEmprestimo,
                         iDevolucao tratadorDeDevolucao,
                         DemonstracaoGuiada demonstracaoGuiada,
                         Clock relogio,
                         Console console,
                         String resumoAmbiente) {
        this.tratadorDePessoa = Objects.requireNonNull(tratadorDePessoa);
        this.tratadorDeEstudante = Objects.requireNonNull(tratadorDeEstudante);
        this.tratadorDeAtendente = Objects.requireNonNull(tratadorDeAtendente);
        this.tratadorDeAcervo = Objects.requireNonNull(tratadorDeAcervo);
        this.tratadorDeEmprestimo = Objects.requireNonNull(tratadorDeEmprestimo);
        this.tratadorDeDevolucao = Objects.requireNonNull(tratadorDeDevolucao);
        this.demonstracaoGuiada = Objects.requireNonNull(demonstracaoGuiada);
        this.relogio = Objects.requireNonNull(relogio);
        this.console = Objects.requireNonNull(console);
        this.resumoAmbiente = Objects.requireNonNull(resumoAmbiente);
    }

    public void iniciar() {
        console.titulo("SISTEMA DE GESTAO DE BIBLIOTECA — PUCPR (10 PADROES GoF)");
        console.linha(resumoAmbiente);

        boolean executando = true;
        while (executando) {
            console.secao("MENU PRINCIPAL");
            console.linha("1 - Cadastros (Pessoa, Estudante, Atendente, Livro) [Administrador]");
            console.linha("2 - Circulacao (Reservar, Consultar Reserva, Emprestar, Renovar) [Estudante]");
            console.linha("3 - Devolucao, Inspecao, Reparo e Multas [Atendente]");
            console.linha("4 - Consulta Publica (Decorator) e Hierarquia do Acervo (Composite)");
            console.linha("5 - Executar Demonstracao Guiada (fluxo completo dos 5 minutos)");
            console.linha("0 - Sair");

            String opcao = console.lerTexto("Escolha uma opcao");
            try {
                switch (opcao) {
                    case "1" -> menuCadastros();
                    case "2" -> menuCirculacao();
                    case "3" -> menuDevolucaoEInspecao();
                    case "4" -> menuConsultaEHierarquia();
                    case "5" -> demonstracaoGuiada.executar();
                    case "0" -> {
                        console.linha("Encerrando aplicacao.");
                        executando = false;
                    }
                    default -> console.linha("Opcao invalida.");
                }
            } catch (RegraDeNegocioException | PersistenciaException | IllegalArgumentException falha) {
                console.linha("[AVISO] " + falha.getMessage());
            }
        }
    }

    private void menuCadastros() {
        console.secao("CADASTROS (R07 — Template Method)");
        console.linha("1 - Inserir Pessoa      | 2 - Listar Pessoas");
        console.linha("3 - Inserir Estudante   | 4 - Listar Estudantes");
        console.linha("5 - Inserir Atendente   | 6 - Listar Atendentes");
        console.linha("7 - Inserir Livro       | 8 - Listar Livros");
        console.linha("9 - Atualizar Livro     | 10 - Excluir Livro");
        console.linha("0 - Voltar");

        switch (console.lerTexto("Opcao")) {
            case "1" -> {
                String nome = console.lerTexto("Nome");
                String sobrenome = console.lerTexto("Sobrenome");
                String cpf = console.lerTexto("CPF (11 digitos)");
                ResultadoDoCadastro<DadosPessoa> res = tratadorDePessoa.inserirPessoa(nome, sobrenome, cpf);
                console.linha(res.mensagem());
            }
            case "2" -> listar("Pessoas", tratadorDePessoa.listarPessoas());
            case "3" -> {
                String cpf = console.lerTexto("CPF da Pessoa ja cadastrada");
                int matricula = console.lerInteiro("Matricula");
                ResultadoDoCadastro<DadosEstudante> res = tratadorDeEstudante.inserirEstudante(cpf, matricula);
                console.linha(res.mensagem());
            }
            case "4" -> listar("Estudantes", tratadorDeEstudante.listarEstudantes());
            case "5" -> {
                String cpf = console.lerTexto("CPF da Pessoa ja cadastrada");
                int idAtendente = console.lerInteiro("ID do Atendente");
                ResultadoDoCadastro<DadosAtendente> res = tratadorDeAtendente.inserirAtendente(cpf, idAtendente);
                console.linha(res.mensagem());
            }
            case "6" -> listar("Atendentes", tratadorDeAtendente.listarAtendentes());
            case "7" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                String titulo = console.lerTexto("Titulo");
                ResultadoDoCadastro<DadosLivro> res = tratadorDeAcervo.inserirLivro(idLivro, titulo);
                console.linha(res.mensagem());
            }
            case "8" -> listar("Livros", tratadorDeAcervo.listarLivros());
            case "9" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                String novoTitulo = console.lerTexto("Novo titulo");
                DadosLivro atualizado = tratadorDeAcervo.atualizarLivro(idLivro, novoTitulo);
                console.linha("Atualizado: " + atualizado);
            }
            case "10" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                tratadorDeAcervo.excluirLivro(idLivro);
                console.linha("Livro " + idLivro + " excluido.");
            }
            case "0" -> {
            }
            default -> console.linha("Opcao invalida.");
        }
    }

    private void menuCirculacao() {
        console.secao("CIRCULACAO — ESTUDANTE (R01 State, R04 Facade, R06 Command)");
        console.linha("1 - Solicitar Reserva (f1 via SolicitacaoDeReserva)");
        console.linha("2 - Consultar Reserva (f2)");
        console.linha("3 - Efetivar Emprestimo (f3)");
        console.linha("4 - Solicitar Renovacao (f4 via SolicitacaoDeRenovacao)");
        console.linha("0 - Voltar");

        switch (console.lerTexto("Opcao")) {
            case "1" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                int matricula = console.lerInteiro("Matricula do Estudante");
                String idSol = lerOuGerarId("ID da solicitacao (ENTER para gerar automaticamente)", "RES");
                // A tela cria o objeto Command (R06) e delega ao TratadorDeEmprestimo
                SolicitacaoDeReserva cmd = new SolicitacaoDeReserva(
                        idSol, "estudante:" + matricula, relogio.instant(), idLivro, matricula);
                ComprovanteDeReserva comp = tratadorDeEmprestimo.executarSolicitacao(cmd);
                console.linha("Reserva registrada: " + comp);
            }
            case "2" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                int matricula = console.lerInteiro("Matricula do Estudante");
                ComprovanteDeReserva comp = tratadorDeEmprestimo.consultarReserva(idLivro, matricula);
                console.linha("Reserva ativa: " + comp);
            }
            case "3" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                int matricula = console.lerInteiro("Matricula do Estudante");
                ComprovanteDeEmprestimo comp = tratadorDeEmprestimo.emprestarLivro(idLivro, matricula);
                console.linha("Emprestimo efetivado: " + comp);
            }
            case "4" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                int matricula = console.lerInteiro("Matricula do Estudante");
                String idSol = lerOuGerarId("ID da solicitacao (ENTER para gerar automaticamente)", "REN");
                // A tela cria o objeto Command (R06) e delega ao TratadorDeEmprestimo
                SolicitacaoDeRenovacao cmd = new SolicitacaoDeRenovacao(
                        idSol, "estudante:" + matricula, relogio.instant(), idLivro, matricula);
                ComprovanteDeRenovacao comp = tratadorDeEmprestimo.executarSolicitacao(cmd);
                console.linha("Renovacao registrada: " + comp);
            }
            case "0" -> {
            }
            default -> console.linha("Opcao invalida.");
        }
    }

    private void menuDevolucaoEInspecao() {
        console.secao("DEVOLUCAO, INSPECAO E MULTAS — ATENDENTE (R01, R02, R03, R05)");
        console.linha("1 - Registrar Devolucao (f5)");
        console.linha("2 - Emitir 2a via de Boleto de Multa (f6)");
        console.linha("3 - Listar Multas");
        console.linha("4 - Inspecionar Livro Devolvido (DEVOLVIDO -> LIVRE ou REPARANDO)");
        console.linha("5 - Enviar Livro Livre para Reparo (T9: LIVRE -> REPARANDO)");
        console.linha("6 - Avaliar Reparo (REPARANDO -> LIVRE ou INCINERADO)");
        console.linha("0 - Voltar");

        switch (console.lerTexto("Opcao")) {
            case "1" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                int matricula = console.lerInteiro("Matricula do Estudante");
                ComprovanteDeDevolucao comp = tratadorDeDevolucao.registrarDevolucao(idLivro, matricula);
                console.linha("Devolucao confirmada: " + comp);
            }
            case "2" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                int matricula = console.lerInteiro("Matricula do Estudante");
                ComprovanteDeMulta multa = tratadorDeDevolucao.emitirBoletoMulta(idLivro, matricula);
                console.linha("Boleto de multa: " + multa);
            }
            case "3" -> listar("Multas", tratadorDeDevolucao.listarMultas());
            case "4" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                boolean aprovado = console.lerSimNao("Livro em bom estado (aprovado)?");
                DadosLivro dados = tratadorDeAcervo.inspecionarLivro(idLivro, aprovado);
                console.linha("Resultado da inspecao: " + dados);
            }
            case "5" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                DadosLivro dados = tratadorDeAcervo.enviarParaReparo(idLivro);
                console.linha("Livro enviado para reparo: " + dados);
            }
            case "6" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                boolean consertado = console.lerSimNao("Livro foi consertado? (n = laudo irrecuperavel)");
                DadosLivro dados = tratadorDeAcervo.avaliarReparo(idLivro, consertado);
                console.linha("Resultado do reparo: " + dados);
            }
            case "0" -> {
            }
            default -> console.linha("Opcao invalida.");
        }
    }

    private void menuConsultaEHierarquia() {
        console.secao("CONSULTA PUBLICA (R09 Decorator) E HIERARQUIA (R10 Composite)");
        console.linha("1 - Consultar Metadados Publicos por ID");
        console.linha("2 - Listar Metadados Publicos");
        console.linha("3 - Criar Grupo (UNIDADE / SECAO / SUBSECAO)");
        console.linha("4 - Vincular Subgrupo a Grupo Pai");
        console.linha("5 - Alocar Livro em Grupo");
        console.linha("6 - Mover Livro para Outro Grupo");
        console.linha("7 - Contar e Listar Livros de um Grupo");
        console.linha("0 - Voltar");

        switch (console.lerTexto("Opcao")) {
            case "1" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                Optional<MetadadosLivro> meta = tratadorDeAcervo.consultarMetadadosPublicos(idLivro);
                console.linha(meta.map(Record::toString).orElse("Livro nao encontrado."));
            }
            case "2" -> listar("Metadados Publicos", tratadorDeAcervo.listarMetadadosPublicos());
            case "3" -> {
                String nome = console.lerTexto("Nome do grupo");
                String tipoStr = console.lerTexto("Tipo (UNIDADE, SECAO ou SUBSECAO)").toUpperCase();
                TipoDeGrupo tipo = TipoDeGrupo.valueOf(tipoStr);
                GrupoDoAcervo grupo = new GrupoDoAcervo(nome, tipo);
                gruposCadastrados.put(grupo.getNome(), grupo);
                console.linha("Grupo criado: " + grupo);
            }
            case "4" -> {
                GrupoDoAcervo pai = exigirGrupo("Nome do grupo pai");
                GrupoDoAcervo filho = exigirGrupo("Nome do subgrupo");
                tratadorDeAcervo.adicionarSubgrupo(pai, filho);
                console.linha("Subgrupo '" + filho.getNome() + "' adicionado em '" + pai.getNome() + "'.");
            }
            case "5" -> {
                GrupoDoAcervo grupo = exigirGrupo("Nome do grupo");
                int idLivro = console.lerInteiro("ID do Livro");
                tratadorDeAcervo.alocarLivroEmGrupo(grupo, idLivro);
                console.linha("Livro " + idLivro + " alocado em '" + grupo.getNome() + "'.");
            }
            case "6" -> {
                int idLivro = console.lerInteiro("ID do Livro");
                GrupoDoAcervo destino = exigirGrupo("Nome do grupo de destino");
                tratadorDeAcervo.moverLivroDeGrupo(idLivro, destino);
                console.linha("Livro " + idLivro + " movido para '" + destino.getNome() + "'.");
            }
            case "7" -> {
                GrupoDoAcervo grupo = exigirGrupo("Nome do grupo");
                console.linha(grupo + " -> total: " + grupo.contarLivros()
                        + " livro(s) | IDs: " + grupo.listarIdsLivros());
            }
            case "0" -> {
            }
            default -> console.linha("Opcao invalida.");
        }
    }

    private GrupoDoAcervo exigirGrupo(String rotulo) {
        String nome = console.lerTexto(rotulo);
        GrupoDoAcervo grupo = gruposCadastrados.get(nome);
        if (grupo == null) {
            throw new RegraDeNegocioException("Grupo nao encontrado na sessao: '" + nome + "'. Crie-o primeiro.");
        }
        return grupo;
    }

    private String lerOuGerarId(String rotulo, String prefixo) {
        String informado = console.lerTexto(rotulo);
        return informado.isEmpty() ? prefixo + "-" + UUID.randomUUID() : informado;
    }

    private void listar(String titulo, List<?> itens) {
        console.linha(titulo + " (" + itens.size() + "):");
        if (itens.isEmpty()) {
            console.linha("  (nenhum registro)");
            return;
        }
        for (Object item : itens) {
            console.linha("  - " + item);
        }
    }
}
