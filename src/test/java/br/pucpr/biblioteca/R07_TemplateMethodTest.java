package br.pucpr.biblioteca;

import br.pucpr.biblioteca.cadastro.CadastroDeAtendente;
import br.pucpr.biblioteca.cadastro.CadastroDeEstudante;
import br.pucpr.biblioteca.cadastro.CadastroDeLivro;
import br.pucpr.biblioteca.cadastro.CadastroDePessoa;
import br.pucpr.biblioteca.dominio.excecao.PreRequisitoNaoAtendidoException;
import br.pucpr.biblioteca.dto.DadosAtendente;
import br.pucpr.biblioteca.dto.DadosEstudante;
import br.pucpr.biblioteca.dto.DadosLivro;
import br.pucpr.biblioteca.dto.DadosPessoa;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;
import br.pucpr.biblioteca.interfaces.iAtendente;
import br.pucpr.biblioteca.interfaces.iEstudante;
import br.pucpr.biblioteca.interfaces.iPessoa;
import br.pucpr.biblioteca.nucleo.AcervoDeLivros;
import br.pucpr.biblioteca.nucleo.Relacionamento;
import br.pucpr.biblioteca.persistencia.AcessoADados;
import br.pucpr.biblioteca.persistencia.memoria.FabricaEmMemoria;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes do Requisito 7 — Template Method (rotina comum para cadastros). */
class R07_TemplateMethodTest {

    private AcessoADados dados;
    private AcervoDeLivros acervo;
    private Relacionamento relacionamento;
    private iPessoa tratadorPessoa;
    private iEstudante tratadorEstudante;
    private iAtendente tratadorAtendente;
    private CadastroDeLivro cadastroLivro;

    @BeforeEach
    void configurar() {
        dados = AcessoADados.criarCom(new FabricaEmMemoria());
        acervo = new AcervoDeLivros(dados.livros());
        relacionamento = new Relacionamento(dados.relacionamento());
        tratadorPessoa = new TratadorDePessoa(
                new CadastroDePessoa(dados.unidadeDeTrabalho(), relacionamento), relacionamento);
        tratadorEstudante = new TratadorDeEstudante(
                new CadastroDeEstudante(dados.unidadeDeTrabalho(), relacionamento), relacionamento);
        tratadorAtendente = new TratadorDeAtendente(
                new CadastroDeAtendente(dados.unidadeDeTrabalho(), relacionamento), relacionamento);
        cadastroLivro = new CadastroDeLivro(dados.unidadeDeTrabalho(), acervo);
    }

    @Test
    void R07_estudanteDuplicadoRetornaCadastroExistente() {
        tratadorPessoa.inserirPessoa("Ana", "Silva", "111.222.333-44");

        ResultadoDoCadastro<DadosEstudante> primeira = tratadorEstudante.inserirEstudante("111.222.333-44", 1001);
        assertFalse(primeira.jaCadastrado());
        assertEquals(1001, primeira.dados().matricula());
        assertEquals("Ana Silva", primeira.dados().nomeCompleto());

        // Tentativa duplicada (mesma matrícula) deve encerrar no passo "buscarExistente" e devolver o cadastro existente
        ResultadoDoCadastro<DadosEstudante> duplicada = tratadorEstudante.inserirEstudante("999.999.999-99", 1001);
        assertTrue(duplicada.jaCadastrado());
        assertEquals("111.222.333-44", duplicada.dados().cpf());
        assertEquals("Ana Silva", duplicada.dados().nomeCompleto());
        assertEquals(1, relacionamento.listarEstudantes().size());
    }

    @Test
    void R07_estudanteComCpfNaoCadastradoFalhaAntesDeGravar() {
        assertThrows(PreRequisitoNaoAtendidoException.class,
                () -> tratadorEstudante.inserirEstudante("555.666.777-88", 2002));

        // Falha no gancho de pré-requisito impede qualquer gravação parcial
        assertTrue(relacionamento.consultarEstudante(2002).isEmpty());
        assertTrue(relacionamento.listarEstudantes().isEmpty());
    }

    @Test
    void R07_atendenteSemPessoaRecusadoSemDadosFicticios() {
        assertThrows(PreRequisitoNaoAtendidoException.class,
                () -> tratadorAtendente.inserirAtendente("444.555.666-77", 501));

        assertTrue(relacionamento.consultarAtendente(501).isEmpty());
        assertTrue(relacionamento.listarAtendentes().isEmpty());

        // Quando a Pessoa existe, o cadastro conclui normalmente e devolve os dados reais
        tratadorPessoa.inserirPessoa("Carlos", "Souza", "444.555.666-77");
        ResultadoDoCadastro<DadosAtendente> cadastrado = tratadorAtendente.inserirAtendente("444.555.666-77", 501);
        assertFalse(cadastrado.jaCadastrado());
        assertEquals("Carlos Souza", cadastrado.dados().nomeCompleto());
    }

    @Test
    void R07_livroEPessoaSeguemMesmaRotinaERetornamDuplicado() {
        ResultadoDoCadastro<DadosLivro> l1 = cadastroLivro.incluir(new CadastroDeLivro.Entrada(10, "Padrões de Projeto"));
        ResultadoDoCadastro<DadosLivro> l2 = cadastroLivro.incluir(new CadastroDeLivro.Entrada(10, "Outro Título"));
        assertFalse(l1.jaCadastrado());
        assertTrue(l2.jaCadastrado());
        assertEquals("Padrões de Projeto", l2.dados().titulo());
        assertEquals("LIVRE", l2.dados().estado());

        ResultadoDoCadastro<DadosPessoa> p1 = tratadorPessoa.inserirPessoa("Maria", "Lima", "123.456.789-00");
        ResultadoDoCadastro<DadosPessoa> p2 = tratadorPessoa.inserirPessoa("Outra", "Pessoa", "123.456.789-00");
        assertFalse(p1.jaCadastrado());
        assertTrue(p2.jaCadastrado());
        assertEquals("Maria", p2.dados().nome());
    }
}
