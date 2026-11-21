package br.pucpr.biblioteca.consulta;

import java.util.List;
import java.util.Optional;

/**
 * <b>R09 — Decorator: papel Component.</b>
 * Contrato único para consulta pública de metadados do acervo, permitindo empilhar recursos
 * opcionais (medição, cache, etc.) sem alterar a implementação original.
 */
public interface ServicoDeConsultaAoAcervo {

    Optional<MetadadosLivro> consultarMetadados(int idLivro);

    List<MetadadosLivro> listarMetadados();
}
