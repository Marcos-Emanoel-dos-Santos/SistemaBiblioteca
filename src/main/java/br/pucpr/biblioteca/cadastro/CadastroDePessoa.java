package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.dto.DadosPessoa;
import br.pucpr.biblioteca.nucleo.Relacionamento;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.Objects;
import java.util.Optional;

/** <b>R07 — Template Method: ConcreteClass para inclusão de {@link Pessoa}.</b> */
public final class CadastroDePessoa extends RotinaDeCadastro<CadastroDePessoa.Entrada, Pessoa, DadosPessoa> {

    public record Entrada(String nome, String sobrenome, String cpf) {
    }

    private final Relacionamento relacionamento;

    public CadastroDePessoa(UnidadeDeTrabalho unidadeDeTrabalho, Relacionamento relacionamento) {
        super(unidadeDeTrabalho);
        this.relacionamento = Objects.requireNonNull(relacionamento, "Relacionamento é obrigatório.");
    }

    @Override
    protected Optional<Pessoa> buscarExistente(Entrada dados) {
        if (dados.cpf() == null || dados.cpf().isBlank()) {
            return Optional.empty();
        }
        return relacionamento.consultarPessoa(dados.cpf().trim());
    }

    @Override
    protected void validar(Entrada dados) {
        ValidadorDeDados.exigirTexto(dados.nome(), "nome");
        ValidadorDeDados.exigirTexto(dados.sobrenome(), "sobrenome");
        ValidadorDeDados.exigirCpf(dados.cpf());
    }

    @Override
    protected Pessoa criar(Entrada dados) {
        return new Pessoa(dados.nome().trim(), dados.sobrenome().trim(), dados.cpf().trim());
    }

    @Override
    protected void armazenar(Pessoa entidade) {
        relacionamento.inserirPessoa(entidade);
    }

    @Override
    protected DadosPessoa apresentar(Pessoa entidade) {
        return DadosPessoa.de(entidade);
    }
}
