package br.pucpr.biblioteca.dominio.estado;

import br.pucpr.biblioteca.dominio.excecao.DadosInvalidosException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Instâncias compartilhadas dos estados do Livro e reconstituição a partir do nome gravado.
 * <p>
 * A persistência grava apenas {@link EstadoLivro#nome()}; ao ler, {@link #porNome(String)}
 * devolve o objeto-estado correspondente. É uma busca em mapa, sem {@code if}/{@code switch}.
 */
public final class EstadosDoLivro {

    public static final EstadoLivro LIVRE = new EstadoLivre();

    private static final Map<String, EstadoLivro> POR_NOME = List.of(LIVRE).stream()
            .collect(Collectors.toUnmodifiableMap(EstadoLivro::nome, Function.identity()));

    private EstadosDoLivro() {
    }

    public static EstadoLivro porNome(String nome) {
        EstadoLivro estado = POR_NOME.get(nome);
        if (estado == null) {
            throw new DadosInvalidosException("Estado de livro desconhecido: " + nome);
        }
        return estado;
    }
}
