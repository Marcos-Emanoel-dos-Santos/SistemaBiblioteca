package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dto.DadosLivro;
import br.pucpr.biblioteca.nucleo.AcervoDeLivros;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.Objects;
import java.util.Optional;

/** <b>R07 — Template Method: ConcreteClass para inclusão de {@link Livro}.</b> */
public final class CadastroDeLivro extends RotinaDeCadastro<CadastroDeLivro.Entrada, Livro, DadosLivro> {

    public record Entrada(int idLivro, String titulo) {
    }

    private final AcervoDeLivros acervo;

    public CadastroDeLivro(UnidadeDeTrabalho unidadeDeTrabalho, AcervoDeLivros acervo) {
        super(unidadeDeTrabalho);
        this.acervo = Objects.requireNonNull(acervo, "Acervo é obrigatório.");
    }

    @Override
    protected Optional<Livro> buscarExistente(Entrada dados) {
        return acervo.consultarLivro(dados.idLivro());
    }

    @Override
    protected void validar(Entrada dados) {
        ValidadorDeDados.exigirIdentificadorPositivo(dados.idLivro(), "idLivro");
        ValidadorDeDados.exigirTexto(dados.titulo(), "título");
    }

    @Override
    protected Livro criar(Entrada dados) {
        return new Livro(dados.idLivro(), dados.titulo().trim());
    }

    @Override
    protected void armazenar(Livro entidade) {
        acervo.inserirLivro(entidade);
    }

    @Override
    protected DadosLivro apresentar(Livro entidade) {
        return DadosLivro.de(entidade);
    }
}
