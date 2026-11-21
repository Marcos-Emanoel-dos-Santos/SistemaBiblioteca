package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.cadastro.CadastroDeLivro;
import br.pucpr.biblioteca.circulacao.devolucao.AgendaDeInspecao;
import br.pucpr.biblioteca.consulta.MetadadosLivro;
import br.pucpr.biblioteca.consulta.ServicoDeConsultaAoAcervo;
import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dominio.excecao.DadosInvalidosException;
import br.pucpr.biblioteca.dominio.excecao.RegistroNaoEncontradoException;
import br.pucpr.biblioteca.dto.DadosLivro;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;
import br.pucpr.biblioteca.interfaces.iLivro;
import br.pucpr.biblioteca.nucleo.AcervoDeLivros;
import br.pucpr.biblioteca.organizacao.GrupoDoAcervo;
import br.pucpr.biblioteca.organizacao.ItemDeLivro;
import br.pucpr.biblioteca.organizacao.OrganizacaoDoAcervo;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Tratador do acervo (componente Biblioteca — implementa {@link iLivro}).
 * <p>
 * Atua estritamente por delegação:
 * <ul>
 *   <li>Inclusão -> {@link CadastroDeLivro} (R07 — Template Method);</li>
 *   <li>Inspeção, reparo e laudo irrecuperável -> eventos do {@link Livro} (R01 — State, sem condicionais de estado);</li>
 *   <li>Consulta pública -> {@link ServicoDeConsultaAoAcervo} (R09 — Decorator);</li>
 *   <li>Organização hierárquica -> {@link OrganizacaoDoAcervo} (R10 — Composite).</li>
 * </ul>
 */
public class TratadorDeAcervo implements iLivro {

    private final UnidadeDeTrabalho unidadeDeTrabalho;
    private final CadastroDeLivro cadastro;
    private final AcervoDeLivros acervo;
    private final ServicoDeConsultaAoAcervo consultaPublica;
    private final OrganizacaoDoAcervo organizacao;
    private final AgendaDeInspecao agendaDeInspecao;

    public TratadorDeAcervo(UnidadeDeTrabalho unidadeDeTrabalho,
                            CadastroDeLivro cadastro,
                            AcervoDeLivros acervo,
                            ServicoDeConsultaAoAcervo consultaPublica,
                            OrganizacaoDoAcervo organizacao,
                            AgendaDeInspecao agendaDeInspecao) {
        this.unidadeDeTrabalho = Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho é obrigatória.");
        this.cadastro = Objects.requireNonNull(cadastro, "CadastroDeLivro é obrigatório.");
        this.acervo = Objects.requireNonNull(acervo, "AcervoDeLivros é obrigatório.");
        this.consultaPublica = Objects.requireNonNull(consultaPublica, "Serviço de consulta é obrigatório.");
        this.organizacao = Objects.requireNonNull(organizacao, "OrganizacaoDoAcervo é obrigatória.");
        this.agendaDeInspecao = Objects.requireNonNull(agendaDeInspecao, "AgendaDeInspecao é obrigatória.");
    }

    @Override
    public ResultadoDoCadastro<DadosLivro> inserirLivro(int idLivro, String titulo) {
        return cadastro.incluir(new CadastroDeLivro.Entrada(idLivro, titulo));
    }

    @Override
    public DadosLivro consultarLivro(int idLivro) {
        return DadosLivro.de(exigirLivro(idLivro));
    }

    @Override
    public DadosLivro atualizarLivro(int idLivro, String novoTitulo) {
        return unidadeDeTrabalho.executar(() -> {
            if (novoTitulo == null || novoTitulo.isBlank()) {
                throw new DadosInvalidosException("Título do livro é obrigatório.");
            }
            Livro livro = exigirLivro(idLivro);
            livro.atualizarLivro(novoTitulo.trim());
            acervo.atualizarLivro(livro);
            return DadosLivro.de(livro);
        });
    }

    @Override
    public void excluirLivro(int idLivro) {
        unidadeDeTrabalho.executar(() -> {
            Livro livro = exigirLivro(idLivro);
            livro.validarExclusao();
            acervo.excluirLivro(idLivro);
            return null;
        });
    }

    @Override
    public List<DadosLivro> listarLivros() {
        return acervo.listarLivros().stream().map(DadosLivro::de).toList();
    }

    // --- Eventos do ciclo de vida do livro delegados ao State (R01) ---

    public DadosLivro inspecionarLivro(int idLivro, boolean aprovado) {
        return unidadeDeTrabalho.executar(() -> {
            Livro livro = exigirLivro(idLivro);
            livro.inspecionarLivro(aprovado);
            acervo.atualizarLivro(livro);
            agendaDeInspecao.concluirInspecao(idLivro);
            return DadosLivro.de(livro);
        });
    }

    public DadosLivro enviarParaReparo(int idLivro) {
        return unidadeDeTrabalho.executar(() -> {
            Livro livro = exigirLivro(idLivro);
            livro.enviarParaReparo();
            acervo.atualizarLivro(livro);
            return DadosLivro.de(livro);
        });
    }

    public DadosLivro retornarDoReparo(int idLivro) {
        return unidadeDeTrabalho.executar(() -> {
            Livro livro = exigirLivro(idLivro);
            livro.retornarDoReparo();
            acervo.atualizarLivro(livro);
            return DadosLivro.de(livro);
        });
    }

    public DadosLivro registrarLaudoIrrecuperavel(int idLivro) {
        return unidadeDeTrabalho.executar(() -> {
            Livro livro = exigirLivro(idLivro);
            livro.registrarLaudoIrrecuperavel();
            acervo.atualizarLivro(livro);
            return DadosLivro.de(livro);
        });
    }

    public DadosLivro avaliarReparo(int idLivro, boolean consertado) {
        return unidadeDeTrabalho.executar(() -> {
            Livro livro = exigirLivro(idLivro);
            livro.avaliarReparo(consertado);
            acervo.atualizarLivro(livro);
            return DadosLivro.de(livro);
        });
    }

    // --- Consulta pública decorada (R09) ---

    public Optional<MetadadosLivro> consultarMetadadosPublicos(int idLivro) {
        return consultaPublica.consultarMetadados(idLivro);
    }

    public List<MetadadosLivro> listarMetadadosPublicos() {
        return consultaPublica.listarMetadados();
    }

    // --- Organização hierárquica do acervo (R10) ---

    public void adicionarSubgrupo(GrupoDoAcervo pai, GrupoDoAcervo filho) {
        organizacao.adicionarSubgrupo(pai, filho);
    }

    public ItemDeLivro alocarLivroEmGrupo(GrupoDoAcervo grupo, int idLivro) {
        exigirLivro(idLivro);
        return organizacao.adicionarLivro(grupo, idLivro);
    }

    public void moverLivroDeGrupo(int idLivro, GrupoDoAcervo destino) {
        organizacao.moverLivro(idLivro, destino);
    }

    public Optional<GrupoDoAcervo> localizarGrupoDoLivro(int idLivro) {
        return organizacao.localizarGrupoDoLivro(idLivro);
    }

    private Livro exigirLivro(int idLivro) {
        return acervo.consultarLivro(idLivro)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Livro não cadastrado: " + idLivro + "."));
    }
}
