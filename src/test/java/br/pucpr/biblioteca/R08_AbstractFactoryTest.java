package br.pucpr.biblioteca;

import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dominio.pessoa.Estudante;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.dominio.transacao.Devolucao;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;
import br.pucpr.biblioteca.dominio.transacao.Reserva;
import br.pucpr.biblioteca.persistencia.AcessoADados;
import br.pucpr.biblioteca.persistencia.FabricaDeAcessoADados;
import br.pucpr.biblioteca.persistencia.FamiliaIncompativelException;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;
import br.pucpr.biblioteca.persistencia.jdbc.FabricaRelacionalMySQL;
import br.pucpr.biblioteca.persistencia.jdbc.RepositorioDeLivrosJdbc;
import br.pucpr.biblioteca.persistencia.jdbc.RepositorioDeMovimentacaoJdbc;
import br.pucpr.biblioteca.persistencia.jdbc.RepositorioDeRelacionamentoJdbc;
import br.pucpr.biblioteca.persistencia.jdbc.UnidadeDeTrabalhoJdbc;
import br.pucpr.biblioteca.persistencia.memoria.FabricaEmMemoria;
import br.pucpr.biblioteca.persistencia.memoria.RepositorioDeLivrosEmMemoria;
import br.pucpr.biblioteca.persistencia.memoria.RepositorioDeMovimentacaoEmMemoria;
import br.pucpr.biblioteca.persistencia.memoria.RepositorioDeRelacionamentoEmMemoria;
import br.pucpr.biblioteca.persistencia.memoria.UnidadeDeTrabalhoEmMemoria;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes do Requisito 8 — Abstract Factory (famílias compatíveis de acesso aos dados). */
class R08_AbstractFactoryTest {

    @Test
    void R08_perfilTesteForneceSomenteObjetosEmMemoria() {
        FabricaDeAcessoADados fabrica = new FabricaEmMemoria();
        AcessoADados acesso = AcessoADados.criarCom(fabrica);

        assertEquals("memoria", fabrica.nomeDaFamilia());
        assertInstanceOf(UnidadeDeTrabalhoEmMemoria.class, acesso.unidadeDeTrabalho());
        assertInstanceOf(RepositorioDeLivrosEmMemoria.class, acesso.livros());
        assertInstanceOf(RepositorioDeMovimentacaoEmMemoria.class, acesso.movimentacao());
        assertInstanceOf(RepositorioDeRelacionamentoEmMemoria.class, acesso.relacionamento());

        assertSame(acesso.unidadeDeTrabalho(), acesso.livros().unidadeDeTrabalho());
        assertSame(acesso.unidadeDeTrabalho(), acesso.movimentacao().unidadeDeTrabalho());
        assertSame(acesso.unidadeDeTrabalho(), acesso.relacionamento().unidadeDeTrabalho());
    }

    @Test
    void R08_perfilProducaoForneceRepositoriosRelacionaisComMesmaUnidadeDeTrabalho() {
        FabricaDeAcessoADados fabrica = new FabricaRelacionalMySQL(
                "jdbc:mysql://localhost:3306/biblioteca", "biblioteca", "biblioteca");
        AcessoADados acesso = AcessoADados.criarCom(fabrica);

        assertEquals("mysql", fabrica.nomeDaFamilia());
        assertInstanceOf(UnidadeDeTrabalhoJdbc.class, acesso.unidadeDeTrabalho());
        assertInstanceOf(RepositorioDeLivrosJdbc.class, acesso.livros());
        assertInstanceOf(RepositorioDeMovimentacaoJdbc.class, acesso.movimentacao());
        assertInstanceOf(RepositorioDeRelacionamentoJdbc.class, acesso.relacionamento());

        // Todos os repositórios da família de produção compartilham exatamente a mesma unidade de trabalho
        assertSame(acesso.unidadeDeTrabalho(), acesso.livros().unidadeDeTrabalho());
        assertSame(acesso.unidadeDeTrabalho(), acesso.movimentacao().unidadeDeTrabalho());
        assertSame(acesso.unidadeDeTrabalho(), acesso.relacionamento().unidadeDeTrabalho());
    }

    @Test
    void R08_misturarFamiliasLancaExcecao() {
        FabricaDeAcessoADados fabricaMemoria = new FabricaEmMemoria();
        FabricaDeAcessoADados fabricaMySQL = new FabricaRelacionalMySQL("jdbc:mysql://localhost:3306/biblioteca");

        UnidadeDeTrabalho uowMemoria = fabricaMemoria.criarUnidadeDeTrabalho();
        UnidadeDeTrabalho uowJdbc = fabricaMySQL.criarUnidadeDeTrabalho();

        assertThrows(FamiliaIncompativelException.class, () -> fabricaMySQL.criarRepositorioDeLivros(uowMemoria));
        assertThrows(FamiliaIncompativelException.class, () -> fabricaMySQL.criarRepositorioDeMovimentacao(uowMemoria));
        assertThrows(FamiliaIncompativelException.class, () -> fabricaMySQL.criarRepositorioDeRelacionamento(uowMemoria));

        assertThrows(FamiliaIncompativelException.class, () -> fabricaMemoria.criarRepositorioDeLivros(uowJdbc));
        assertThrows(FamiliaIncompativelException.class, () -> fabricaMemoria.criarRepositorioDeMovimentacao(uowJdbc));
        assertThrows(FamiliaIncompativelException.class, () -> fabricaMemoria.criarRepositorioDeRelacionamento(uowJdbc));
    }

    @Test
    void R08_jdbcCompartilhaConexaoECommitRollback() {
        FabricaRelacionalMySQL fabrica = new FabricaRelacionalMySQL("jdbc:sqlite::memory:");
        AcessoADados acesso = AcessoADados.criarCom(fabrica);

        try (UnidadeDeTrabalhoJdbc uow = (UnidadeDeTrabalhoJdbc) acesso.unidadeDeTrabalho()) {
            uow.inicializarEsquema();
            Connection conexaoInicial = uow.obterConexao();

            // 1) Commit multi-repositório na mesma transação e conexão
            uow.executar(() -> {
                acesso.livros().salvar(new Livro(1, "Clean Architecture"));
                acesso.relacionamento().salvarPessoa(new Pessoa("Joao", "Pedro", "111.222.333-44"));
                acesso.relacionamento().salvarEstudante(new Estudante("111.222.333-44", 101));
                acesso.movimentacao().salvarReserva(
                        new Reserva("R-1", 1, 101, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4)));
                acesso.movimentacao().salvarEmprestimo(
                        new Emprestimo("E-1", 1, 101, LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 16), 0));
                acesso.movimentacao().salvarDevolucao(
                        new Devolucao("D-1", 1, 101, LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 16)));
                return null;
            });

            assertSame(conexaoInicial, uow.obterConexao());
            assertEquals("Clean Architecture", acesso.livros().buscar(1).orElseThrow().getTitulo());
            assertEquals("Joao Pedro", acesso.relacionamento().buscarPessoa("111.222.333-44").orElseThrow().getNomeCompleto());
            assertTrue(acesso.movimentacao().buscarReserva(1, 101).isPresent());
            assertTrue(acesso.movimentacao().buscarEmprestimo(1, 101).isPresent());
            assertEquals(4, acesso.movimentacao().buscarDevolucao(1, 101).orElseThrow().getDiasDeAtraso());

            // 2) Rollback multi-repositório: falha no meio desfaz TUDO (Livro + Pessoa)
            assertThrows(RuntimeException.class, () -> uow.executar(() -> {
                acesso.livros().salvar(new Livro(2, "Livro Que Deve Ser Desfeito"));
                acesso.relacionamento().salvarPessoa(new Pessoa("Fantasma", "Rollback", "999.888.777-66"));
                throw new IllegalStateException("Falha simulada no meio da transação");
            }));

            assertTrue(acesso.livros().buscar(2).isEmpty());
            assertTrue(acesso.relacionamento().buscarPessoa("999.888.777-66").isEmpty());
        }
    }
}
