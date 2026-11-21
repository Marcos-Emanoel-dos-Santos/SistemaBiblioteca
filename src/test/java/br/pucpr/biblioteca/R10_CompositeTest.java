package br.pucpr.biblioteca;

import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dominio.excecao.RegraDeNegocioException;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;
import br.pucpr.biblioteca.nucleo.AcervoDeLivros;
import br.pucpr.biblioteca.nucleo.Movimentacao;
import br.pucpr.biblioteca.organizacao.ComponenteDoAcervo;
import br.pucpr.biblioteca.organizacao.GrupoDoAcervo;
import br.pucpr.biblioteca.organizacao.ItemDeLivro;
import br.pucpr.biblioteca.organizacao.OrganizacaoDoAcervo;
import br.pucpr.biblioteca.organizacao.TipoDeGrupo;
import br.pucpr.biblioteca.persistencia.AcessoADados;
import br.pucpr.biblioteca.persistencia.memoria.FabricaEmMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes do Requisito 10 — Composite (organização hierárquica do acervo). */
class R10_CompositeTest {

    @Test
    void R10_unidadeComDuasSecoesInformaCincoLivros() {
        OrganizacaoDoAcervo organizacao = new OrganizacaoDoAcervo();
        GrupoDoAcervo unidadeCentral = new GrupoDoAcervo("Biblioteca Central", TipoDeGrupo.UNIDADE);
        GrupoDoAcervo secaoComputacao = new GrupoDoAcervo("Computação", TipoDeGrupo.SECAO);
        GrupoDoAcervo subsecaoPadroes = new GrupoDoAcervo("Padrões de Projeto", TipoDeGrupo.SUBSECAO);
        GrupoDoAcervo secaoFilosofia = new GrupoDoAcervo("Filosofia", TipoDeGrupo.SECAO);

        organizacao.adicionarSubgrupo(unidadeCentral, secaoComputacao);
        organizacao.adicionarSubgrupo(secaoComputacao, subsecaoPadroes);
        organizacao.adicionarSubgrupo(unidadeCentral, secaoFilosofia);

        // 3 livros na seção Computação (2 diretos + 1 em subseção) e 2 na seção Filosofia
        ItemDeLivro item1 = organizacao.adicionarLivro(secaoComputacao, 101);
        organizacao.adicionarLivro(secaoComputacao, 102);
        organizacao.adicionarLivro(subsecaoPadroes, 103);
        organizacao.adicionarLivro(secaoFilosofia, 201);
        organizacao.adicionarLivro(secaoFilosofia, 202);

        // Tratamento uniforme via contrato ComponenteDoAcervo (grupos e folhas)
        ComponenteDoAcervo raiz = unidadeCentral;
        ComponenteDoAcervo folha = item1;

        assertEquals(1, folha.contarLivros());
        assertEquals(List.of(101), folha.listarIdsLivros());
        assertEquals(3, secaoComputacao.contarLivros());
        assertEquals(2, secaoFilosofia.contarLivros());
        assertEquals(5, raiz.contarLivros());
        assertEquals(List.of(103, 101, 102, 201, 202), raiz.listarIdsLivros());
    }

    @Test
    void R10_moverLivroNaoAlteraIdEstadoNemEmprestimo() {
        AcessoADados dados = AcessoADados.criarCom(new FabricaEmMemoria());
        AcervoDeLivros acervo = new AcervoDeLivros(dados.livros());
        Movimentacao movimentacao = new Movimentacao(dados.movimentacao());

        Livro livro = new Livro(77, "Domain-Driven Design");
        acervo.inserirLivro(livro);
        Emprestimo emprestimo = new Emprestimo(
                "EMP-77", 77, 1001, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 24), 1);
        movimentacao.criarEmprestimo(emprestimo);

        OrganizacaoDoAcervo organizacao = new OrganizacaoDoAcervo();
        GrupoDoAcervo secaoA = new GrupoDoAcervo("Seção A", TipoDeGrupo.SECAO);
        GrupoDoAcervo secaoB = new GrupoDoAcervo("Seção B", TipoDeGrupo.SECAO);
        organizacao.adicionarLivro(secaoA, 77);

        // Move o livro entre seções
        organizacao.moverLivro(77, secaoB);

        assertEquals(0, secaoA.contarLivros());
        assertEquals(1, secaoB.contarLivros());
        assertEquals(secaoB, organizacao.localizarGrupoDoLivro(77).orElseThrow());

        // Identidade, estado do Livro e empréstimo em Movimentacao permanecem intactos
        Livro livroApos = acervo.consultarLivro(77).orElseThrow();
        assertEquals(77, livroApos.getIdLivro());
        assertEquals("LIVRE", livroApos.getEstado().nome());
        Emprestimo emprestimoApos = movimentacao.consultarEmprestimo(77, 1001).orElseThrow();
        assertEquals("EMP-77", emprestimoApos.getId());
        assertEquals(LocalDate.of(2026, 11, 24), emprestimoApos.getDataDeVencimento());
        assertEquals(1, emprestimoApos.getNumRenovacoes());
    }

    @Test
    void R10_grupoRecusaCiclo() {
        GrupoDoAcervo unidade = new GrupoDoAcervo("Unidade A", TipoDeGrupo.UNIDADE);
        GrupoDoAcervo secao = new GrupoDoAcervo("Seção B", TipoDeGrupo.SECAO);
        GrupoDoAcervo subsecao = new GrupoDoAcervo("Subseção C", TipoDeGrupo.SUBSECAO);

        unidade.adicionar(secao);
        secao.adicionar(subsecao);

        // Auto-ciclo
        assertThrows(RegraDeNegocioException.class, () -> unidade.adicionar(unidade));
        // Ciclo indireto (ancestral adicionado como filho de descendente)
        assertThrows(RegraDeNegocioException.class, () -> subsecao.adicionar(unidade));
        assertThrows(RegraDeNegocioException.class, () -> subsecao.adicionar(secao));
    }

    @Test
    void R10_livroNaoPodeEstarEmDoisGrupos() {
        OrganizacaoDoAcervo organizacao = new OrganizacaoDoAcervo();
        GrupoDoAcervo unidade = new GrupoDoAcervo("Unidade Única", TipoDeGrupo.UNIDADE);
        GrupoDoAcervo secao1 = new GrupoDoAcervo("Seção 1", TipoDeGrupo.SECAO);
        GrupoDoAcervo secao2 = new GrupoDoAcervo("Seção 2", TipoDeGrupo.SECAO);
        GrupoDoAcervo outraUnidade = new GrupoDoAcervo("Outra Unidade", TipoDeGrupo.UNIDADE);

        organizacao.adicionarSubgrupo(unidade, secao1);
        organizacao.adicionarSubgrupo(unidade, secao2);

        organizacao.adicionarLivro(secao1, 500);

        // Mesmo na mesma árvore (direto no GrupoDoAcervo)
        assertThrows(RegraDeNegocioException.class, () -> secao2.adicionar(new ItemDeLivro(500)));
        // E via OrganizacaoDoAcervo, mesmo em outra unidade isolada
        assertThrows(RegraDeNegocioException.class, () -> organizacao.adicionarLivro(outraUnidade, 500));

        assertTrue(secao1.listarIdsLivros().contains(500));
        assertTrue(secao2.listarIdsLivros().isEmpty());
    }
}
