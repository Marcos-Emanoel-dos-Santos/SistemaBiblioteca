package br.pucpr.biblioteca.circulacao.solicitacao;

import br.pucpr.biblioteca.dominio.excecao.DadosInvalidosException;
import br.pucpr.biblioteca.dto.ComprovanteDeReserva;
import br.pucpr.biblioteca.interfaces.iCirculacao;

import java.time.Instant;
import java.util.Objects;

/** <b>R06 — Command: ConcreteCommand para solicitação de reserva de livro.</b> */
public final class SolicitacaoDeReserva implements SolicitacaoDeCirculacao<ComprovanteDeReserva> {

    private final String id;
    private final String autor;
    private final Instant instanteCriacao;
    private final int idLivro;
    private final int matricula;

    public SolicitacaoDeReserva(String id, String autor, Instant instanteCriacao, int idLivro, int matricula) {
        if (id == null || id.isBlank()) {
            throw new DadosInvalidosException("Identificador da solicitação é obrigatório.");
        }
        if (autor == null || autor.isBlank()) {
            throw new DadosInvalidosException("Autor da solicitação é obrigatório.");
        }
        this.id = id.trim();
        this.autor = autor.trim();
        this.instanteCriacao = Objects.requireNonNull(instanteCriacao, "Instante de criação é obrigatório.");
        this.idLivro = idLivro;
        this.matricula = matricula;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAutor() {
        return autor;
    }

    @Override
    public Instant getInstanteCriacao() {
        return instanteCriacao;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public int getMatricula() {
        return matricula;
    }

    @Override
    public ComprovanteDeReserva executar(iCirculacao circulacao) {
        return circulacao.reservarLivro(idLivro, matricula);
    }
}
