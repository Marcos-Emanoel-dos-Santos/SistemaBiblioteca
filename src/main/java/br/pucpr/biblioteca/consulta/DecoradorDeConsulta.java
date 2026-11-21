package br.pucpr.biblioteca.consulta;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** <b>R09 — Decorator: papel Decorator abstrato.</b> */
public abstract class DecoradorDeConsulta implements ServicoDeConsultaAoAcervo {

    protected final ServicoDeConsultaAoAcervo delegado;

    protected DecoradorDeConsulta(ServicoDeConsultaAoAcervo delegado) {
        this.delegado = Objects.requireNonNull(delegado, "Serviço delegado é obrigatório.");
    }

    @Override
    public Optional<MetadadosLivro> consultarMetadados(int idLivro) {
        return delegado.consultarMetadados(idLivro);
    }

    @Override
    public List<MetadadosLivro> listarMetadados() {
        return delegado.listarMetadados();
    }
}
