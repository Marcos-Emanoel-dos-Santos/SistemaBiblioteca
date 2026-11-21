package br.pucpr.biblioteca.dominio.estado;

import br.pucpr.biblioteca.dominio.excecao.DadosInvalidosException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Instâncias compartilhadas dos sete estados do Livro e reconstituição a partir do nome gravado.
 * <p>
 * A persistência grava apenas {@link EstadoLivro#nome()}; ao ler, {@link #porNome(String)}
 * devolve o objeto-estado correspondente por busca em mapa, sem {@code if}/{@code switch}.
 */
public final class EstadosDoLivro {

    public static final EstadoLivro LIVRE = new EstadoLivre();
    public static final EstadoLivro RESERVADO = new EstadoReservado();
    public static final EstadoLivro EMPRESTADO = new EstadoEmprestado();
    public static final EstadoLivro RENOVADO = new EstadoRenovado();
    public static final EstadoLivro DEVOLVIDO = new EstadoDevolvido();
    public static final EstadoLivro REPARANDO = new EstadoReparando();
    public static final EstadoLivro INCINERADO = new EstadoIncinerado();

    private static final Map<String, EstadoLivro> POR_NOME = List.of(
                    LIVRE, RESERVADO, EMPRESTADO, RENOVADO, DEVOLVIDO, REPARANDO, INCINERADO)
            .stream()
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
