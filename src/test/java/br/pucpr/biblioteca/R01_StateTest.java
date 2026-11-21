package br.pucpr.biblioteca;

import br.pucpr.biblioteca.apoio.CenarioDeTeste;
import br.pucpr.biblioteca.config.MontadorDoSistema;
import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dominio.excecao.RegraDeNegocioException;
import br.pucpr.biblioteca.dominio.excecao.TransicaoInvalidaException;
import br.pucpr.biblioteca.dto.ComprovanteDeEmprestimo;
import br.pucpr.biblioteca.dto.ComprovanteDeRenovacao;
import br.pucpr.biblioteca.dto.DadosLivro;
import br.pucpr.biblioteca.persistencia.AcessoADados;
import br.pucpr.biblioteca.persistencia.jdbc.FabricaRelacionalMySQL;
import br.pucpr.biblioteca.persistencia.jdbc.UnidadeDeTrabalhoJdbc;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Testes do Requisito 1 — State (ciclo de vida do livro). */
class R01_StateTest {

    @Test
    void R01_terceiraRenovacaoRecusada() {
        CenarioDeTeste cenario = CenarioDeTeste.criar(LocalDate.of(2026, 11, 1)).cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        ComprovanteDeEmprestimo emp = s.tratadorDeEmprestimo().emprestarLivro(1, 1001);
        assertEquals("EMPRESTADO", emp.estadoDoLivro());
        LocalDate vencimentoInicial = emp.dataDeVencimento();

        // 1ª renovação (EMPRESTADO -> RENOVADO, +30 dias)
        ComprovanteDeRenovacao ren1 = s.tratadorDeEmprestimo().renovarEmprestimo(1, 1001);
        assertEquals(1, ren1.numRenovacoes());
        assertEquals(vencimentoInicial.plusDays(30), ren1.novoVencimento());
        assertEquals("RENOVADO", ren1.estadoDoLivro());

        // 2ª renovação (RENOVADO -> RENOVADO, +30 dias)
        ComprovanteDeRenovacao ren2 = s.tratadorDeEmprestimo().renovarEmprestimo(1, 1001);
        assertEquals(2, ren2.numRenovacoes());
        LocalDate vencimentoAposSegunda = vencimentoInicial.plusDays(60);
        assertEquals(vencimentoAposSegunda, ren2.novoVencimento());

        // 3ª renovação deve ser recusada SEM alterar o vencimento nem o estado
        assertThrows(RegraDeNegocioException.class,
                () -> s.tratadorDeEmprestimo().renovarEmprestimo(1, 1001));

        var emprestimoPersistido = s.acessoADados().movimentacao().buscarEmprestimo(1, 1001).orElseThrow();
        assertEquals(2, emprestimoPersistido.getNumRenovacoes());
        assertEquals(vencimentoAposSegunda, emprestimoPersistido.getDataDeVencimento());
        assertEquals("RENOVADO", s.tratadorDeAcervo().consultarLivro(1).estado());
    }

    @Test
    void R01_livroDevolvidoNaoInspecionadoNaoPodeSerReservado() {
        CenarioDeTeste cenario = CenarioDeTeste.criar().cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        s.tratadorDeEmprestimo().emprestarLivro(1, 1001);
        s.tratadorDeDevolucao().registrarDevolucao(1, 1001);

        assertEquals("DEVOLVIDO", s.tratadorDeAcervo().consultarLivro(1).estado());

        // Livro devolvido ainda não inspecionado recusa reserva sem mudar de estado
        assertThrows(TransicaoInvalidaException.class,
                () -> s.tratadorDeEmprestimo().reservarLivro(1, 1001));
        assertEquals("DEVOLVIDO", s.tratadorDeAcervo().consultarLivro(1).estado());

        // Após inspeção aprovada, volta a LIVRE e aceita nova reserva
        DadosLivro aposInspecao = s.tratadorDeAcervo().inspecionarLivro(1, true);
        assertEquals("LIVRE", aposInspecao.estado());
        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        assertEquals("RESERVADO", s.tratadorDeAcervo().consultarLivro(1).estado());
    }

    @Test
    void R01_laudoIrrecuperavelLevaReparandoAIncinerado() {
        CenarioDeTeste cenario = CenarioDeTeste.criar().cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        s.tratadorDeEmprestimo().emprestarLivro(1, 1001);
        s.tratadorDeDevolucao().registrarDevolucao(1, 1001);

        // Inspeção reprovada -> REPARANDO
        assertEquals("REPARANDO", s.tratadorDeAcervo().inspecionarLivro(1, false).estado());
        // Em reparo não aceita reserva
        assertThrows(TransicaoInvalidaException.class,
                () -> s.tratadorDeEmprestimo().reservarLivro(1, 1001));

        // Laudo irrecuperável via avaliarReparo(false) -> delega a registrarLaudoIrrecuperavel() -> INCINERADO
        assertEquals("INCINERADO", s.tratadorDeAcervo().avaliarReparo(1, false).estado());

        // Estado final INCINERADO recusa qualquer operação
        assertThrows(TransicaoInvalidaException.class, () -> s.tratadorDeAcervo().retornarDoReparo(1));
        assertThrows(TransicaoInvalidaException.class, () -> s.tratadorDeEmprestimo().reservarLivro(1, 1001));
        assertEquals("INCINERADO", s.tratadorDeAcervo().consultarLivro(1).estado());
    }

    @Test
    void R01_eventoInvalidoNaoAlteraLivro() {
        CenarioDeTeste cenario = CenarioDeTeste.criar().cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        // Q2: LIVRE não vai direto para INCINERADO, mas aceita T9 enviarParaReparo -> REPARANDO
        assertThrows(TransicaoInvalidaException.class,
                () -> s.tratadorDeAcervo().registrarLaudoIrrecuperavel(1));
        assertEquals("LIVRE", s.tratadorDeAcervo().consultarLivro(1).estado());

        assertEquals("REPARANDO", s.tratadorDeAcervo().enviarParaReparo(1).estado());
        assertEquals("LIVRE", s.tratadorDeAcervo().retornarDoReparo(1).estado());
    }

    @Test
    void R01_estadoPersistidoEReconstituido() {
        FabricaRelacionalMySQL fabrica = new FabricaRelacionalMySQL("jdbc:sqlite::memory:");
        AcessoADados acesso = AcessoADados.criarCom(fabrica);

        try (UnidadeDeTrabalhoJdbc uow = (UnidadeDeTrabalhoJdbc) acesso.unidadeDeTrabalho()) {
            uow.inicializarEsquema();

            Livro livro = new Livro(42, "Arquitetura Limpa");
            livro.enviarParaReparo();
            assertEquals("REPARANDO", livro.getEstado().nome());
            acesso.livros().salvar(livro);

            // Reconstitui a partir do banco JDBC e valida que o objeto-estado correto responde aos eventos
            Livro recarregado = acesso.livros().buscar(42).orElseThrow();
            assertEquals("REPARANDO", recarregado.getEstado().nome());
            assertThrows(TransicaoInvalidaException.class, recarregado::reservarLivro);

            recarregado.registrarLaudoIrrecuperavel();
            acesso.livros().salvar(recarregado);

            Livro finalRecarregado = acesso.livros().buscar(42).orElseThrow();
            assertEquals("INCINERADO", finalRecarregado.getEstado().nome());
            assertThrows(TransicaoInvalidaException.class, finalRecarregado::retornarDoReparo);
        }
    }
}
