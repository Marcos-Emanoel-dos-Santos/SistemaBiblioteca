package br.pucpr.biblioteca.consulta;

import br.pucpr.biblioteca.nucleo.AcervoDeLivros;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <b>R09 — Decorator: papel ConcreteComponent (implementação original sem alterações).</b>
 * Consulta diretamente {@link AcervoDeLivros} e projeta apenas os metadados públicos.
 */
public final class ConsultaAoAcervo implements ServicoDeConsultaAoAcervo {

    private final AcervoDeLivros acervo;
    private int consultasRealizadasNaFonte;

    public ConsultaAoAcervo(AcervoDeLivros acervo) {
        this.acervo = Objects.requireNonNull(acervo, "Acervo é obrigatório.");
    }

    @Override
    public Optional<MetadadosLivro> consultarMetadados(int idLivro) {
        consultasRealizadasNaFonte++;
        return acervo.consultarLivro(idLivro).map(MetadadosLivro::de);
    }

    @Override
    public List<MetadadosLivro> listarMetadados() {
        consultasRealizadasNaFonte++;
        return acervo.listarLivros().stream().map(MetadadosLivro::de).toList();
    }

    public int getConsultasRealizadasNaFonte() {
        return consultasRealizadasNaFonte;
    }
}
