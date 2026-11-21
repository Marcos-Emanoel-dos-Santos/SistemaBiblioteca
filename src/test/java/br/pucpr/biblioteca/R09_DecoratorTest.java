package br.pucpr.biblioteca;

import br.pucpr.biblioteca.apoio.CenarioDeTeste;
import br.pucpr.biblioteca.config.MontadorDoSistema;
import br.pucpr.biblioteca.consulta.ConsultaAoAcervo;
import br.pucpr.biblioteca.consulta.ConsultaComCache;
import br.pucpr.biblioteca.consulta.ConsultaComMedicao;
import br.pucpr.biblioteca.consulta.MetadadosLivro;
import br.pucpr.biblioteca.consulta.RegistroDeMedicoes;
import br.pucpr.biblioteca.consulta.ServicoDeConsultaAoAcervo;
import br.pucpr.biblioteca.dominio.excecao.TransicaoInvalidaException;
import br.pucpr.biblioteca.nucleo.AcervoDeLivros;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes do Requisito 9 — Decorator (serviço de consulta ao acervo com recursos opcionais). */
class R09_DecoratorTest {

    @Test
    void R09_segundaConsultaUsaCacheEGeraMedicao() {
        CenarioDeTeste cenario = CenarioDeTeste.criar().cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        // 1ª consulta: miss no cache -> vai à fonte original + gera medição
        Optional<MetadadosLivro> primeira = s.tratadorDeAcervo().consultarMetadadosPublicos(1);
        assertTrue(primeira.isPresent());
        assertEquals("Padrões de Projeto GoF", primeira.get().titulo());
        assertEquals(1, s.consultaOriginal().getConsultasRealizadasNaFonte());
        assertEquals(0, s.consultaComCache().getAcertosNoCache());
        assertEquals(1, s.registroDeMedicoes().quantidade());

        // 2ª consulta: hit no cache (não toca a fonte original) e AINDA gera medição (ordem: Medicao(Cache(Original)))
        Optional<MetadadosLivro> segunda = s.tratadorDeAcervo().consultarMetadadosPublicos(1);
        assertEquals(primeira, segunda);
        assertEquals(1, s.consultaOriginal().getConsultasRealizadasNaFonte());
        assertEquals(1, s.consultaComCache().getAcertosNoCache());
        assertEquals(2, s.registroDeMedicoes().quantidade());
    }

    @Test
    void R09_cacheAntigoNaoAutorizaReservaDeLivroNaoLivre() {
        CenarioDeTeste cenario = CenarioDeTeste.criar().cadastrarDadosBasicos();
        MontadorDoSistema.SistemaMontado s = cenario.sistema();

        // Popula o cache enquanto o livro está LIVRE
        assertTrue(s.tratadorDeAcervo().consultarMetadadosPublicos(1).isPresent());

        // Reserva o livro (agora está RESERVADO na fonte autoritativa)
        s.tratadorDeEmprestimo().reservarLivro(1, 1001);
        assertEquals("RESERVADO", s.tratadorDeAcervo().consultarLivro(1).estado());

        // A consulta pública continua respondendo do cache com metadados (sem estado)
        assertTrue(s.tratadorDeAcervo().consultarMetadadosPublicos(1).isPresent());
        assertEquals(1, s.consultaComCache().getAcertosNoCache());

        // Mesmo com o livro no cache, nova tentativa de reserva consulta a fonte autoritativa e é recusada
        s.tratadorDePessoa().inserirPessoa("Clara", "Mendes", "333.444.555-66");
        s.tratadorDeEstudante().inserirEstudante("333.444.555-66", 1002);
        assertThrows(TransicaoInvalidaException.class,
                () -> s.tratadorDeEmprestimo().reservarLivro(1, 1002));
    }

    @Test
    void R09_falhaNoCacheRecorreAoServicoOriginal() {
        CenarioDeTeste cenario = CenarioDeTeste.criar().cadastrarDadosBasicos();
        AcervoDeLivros acervo = new AcervoDeLivros(cenario.sistema().acessoADados().livros());
        ConsultaAoAcervo original = new ConsultaAoAcervo(acervo);

        ConsultaComCache.ArmazenamentoDeCache cacheDefeituoso = new ConsultaComCache.ArmazenamentoDeCache() {
            @Override
            public MetadadosLivro buscar(int idLivro) {
                throw new IllegalStateException("Cache indisponível na leitura");
            }

            @Override
            public void salvar(int idLivro, MetadadosLivro metadados) {
                throw new IllegalStateException("Cache indisponível na escrita");
            }

            @Override
            public void invalidar(int idLivro) {
                throw new IllegalStateException("Cache indisponível");
            }

            @Override
            public void limpar() {
            }
        };

        ConsultaComCache comCache = new ConsultaComCache(original, cacheDefeituoso);
        RegistroDeMedicoes medicoes = new RegistroDeMedicoes();
        ServicoDeConsultaAoAcervo servico = new ConsultaComMedicao(comCache, medicoes);

        Optional<MetadadosLivro> resultado = servico.consultarMetadados(1);
        assertTrue(resultado.isPresent());
        assertEquals("Padrões de Projeto GoF", resultado.get().titulo());
        assertEquals(1, original.getConsultasRealizadasNaFonte());
        assertEquals(2, comCache.getFalhasDeCacheIgnoradas()); // leitura + escrita ignoradas com fallback
        assertEquals(1, medicoes.quantidade());
    }
}
