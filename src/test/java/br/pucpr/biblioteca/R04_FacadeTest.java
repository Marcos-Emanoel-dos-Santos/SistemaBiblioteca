package br.pucpr.biblioteca;

import br.pucpr.biblioteca.apoio.CenarioDeTeste;
import br.pucpr.biblioteca.circulacao.FachadaDeCirculacao;
import br.pucpr.biblioteca.config.MontadorDoSistema;
import br.pucpr.biblioteca.config.RelogioAjustavel;
import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dto.ComprovanteDeEmprestimo;
import br.pucpr.biblioteca.dto.ComprovanteDeReserva;
import br.pucpr.biblioteca.nucleo.AcervoDeLivros;
import br.pucpr.biblioteca.nucleo.Movimentacao;
import br.pucpr.biblioteca.nucleo.Relacionamento;
import br.pucpr.biblioteca.persistencia.AcessoADados;
import br.pucpr.biblioteca.persistencia.RepositorioDeLivros;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;
import br.pucpr.biblioteca.persistencia.memoria.FabricaEmMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes do Requisito 4 — Facade (entrada única para as operações de circulação). */
class R04_FacadeTest {

    @Test
    void R04_emprestimoValidaReservaRemoveReservaCriaEmprestimoEAtualizaLivro() {
        CenarioDeTeste cenario = CenarioDeTeste.criar(LocalDate.of(2026, 11, 1)).cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        assertTrue(s.acessoADados().movimentacao().buscarReserva(1, 1001).isPresent());

        ComprovanteDeEmprestimo comprovante = s.tratadorDeEmprestimo().emprestarLivro(1, 1001);

        assertEquals(1, comprovante.idLivro());
        assertEquals(1001, comprovante.matricula());
        assertEquals("EMPRESTADO", comprovante.estadoDoLivro());
        assertTrue(s.acessoADados().movimentacao().buscarReserva(1, 1001).isEmpty());
        assertTrue(s.acessoADados().movimentacao().buscarEmprestimo(1, 1001).isPresent());
        assertEquals("EMPRESTADO", s.tratadorDeAcervo().consultarLivro(1).estado());
    }

    @Test
    void R04_falhaNoMeioDesfazTodaOperacao() {
        AcessoADados dados = AcessoADados.criarCom(new FabricaEmMemoria());
        Relacionamento relacionamento = new Relacionamento(dados.relacionamento());
        Movimentacao movimentacao = new Movimentacao(dados.movimentacao());

        // Repositório que simula falha técnica ao atualizar o livro para EMPRESTADO (após remover reserva e criar empréstimo)
        RepositorioDeLivros repoComFalha = new RepositorioDeLivros() {
            @Override
            public UnidadeDeTrabalho unidadeDeTrabalho() {
                return dados.unidadeDeTrabalho();
            }

            @Override
            public void salvar(Livro livro) {
                if ("EMPRESTADO".equals(livro.getEstado().nome())) {
                    throw new IllegalStateException("Falha simulada ao persistir atualização do livro");
                }
                dados.livros().salvar(livro);
            }

            @Override
            public Optional<Livro> buscar(int idLivro) {
                return dados.livros().buscar(idLivro);
            }

            @Override
            public void remover(int idLivro) {
                dados.livros().remover(idLivro);
            }

            @Override
            public List<Livro> listar() {
                return dados.livros().listar();
            }
        };

        AcervoDeLivros acervo = new AcervoDeLivros(repoComFalha);
        FachadaDeCirculacao fachada = new FachadaDeCirculacao(
                dados.unidadeDeTrabalho(), acervo, relacionamento, movimentacao,
                RelogioAjustavel.em(LocalDate.of(2026, 11, 1)), 3, 14, 30);

        dados.relacionamento().salvarPessoa(new br.pucpr.biblioteca.dominio.pessoa.Pessoa("Ana", "Silva", "111.222.333-44"));
        dados.relacionamento().salvarEstudante(new br.pucpr.biblioteca.dominio.pessoa.Estudante("111.222.333-44", 1001));
        dados.livros().salvar(new Livro(1, "Sistemas Distribuídos"));

        fachada.reservarLivro(1, 1001);

        assertThrows(IllegalStateException.class, () -> fachada.emprestarLivro(1, 1001));

        // Rollback completo: reserva permanece ativa, empréstimo não existe e livro segue RESERVADO
        assertTrue(dados.movimentacao().buscarReserva(1, 1001).isPresent());
        assertTrue(dados.movimentacao().buscarEmprestimo(1, 1001).isEmpty());
        assertEquals("RESERVADO", dados.livros().buscar(1).orElseThrow().getEstado().nome());
    }

    @Test
    void R04_fachadaRetornaComprovanteSemExporColecoes() {
        CenarioDeTeste cenario = CenarioDeTeste.criar().cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        ComprovanteDeReserva reserva = s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        ComprovanteDeReserva consultada = s.tratadorDeEmprestimo().consultarReserva(1, 1001);

        assertInstanceOf(ComprovanteDeReserva.class, consultada);
        assertEquals(reserva.idReserva(), consultada.idReserva());
        assertEquals("Padrões de Projeto GoF", consultada.titulo());
    }
}
