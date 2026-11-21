package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dominio.excecao.PreRequisitoNaoAtendidoException;
import br.pucpr.biblioteca.dominio.pessoa.Estudante;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.dto.DadosEstudante;
import br.pucpr.biblioteca.nucleo.Relacionamento;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.Objects;
import java.util.Optional;

/**
 * <b>R07 — Template Method: ConcreteClass para inclusão de {@link Estudante}.</b>
 * Sobrescreve o gancho {@link #verificarPreRequisitos(Entrada)} para exigir que o CPF informado
 * pertença a uma {@link Pessoa} já cadastrada no RH antes de qualquer criação ou gravação.
 */
public final class CadastroDeEstudante
        extends RotinaDeCadastro<CadastroDeEstudante.Entrada, Estudante, DadosEstudante> {

    public record Entrada(String cpf, int matricula) {
    }

    private final Relacionamento relacionamento;

    public CadastroDeEstudante(UnidadeDeTrabalho unidadeDeTrabalho, Relacionamento relacionamento) {
        super(unidadeDeTrabalho);
        this.relacionamento = Objects.requireNonNull(relacionamento, "Relacionamento é obrigatório.");
    }

    @Override
    protected Optional<Estudante> buscarExistente(Entrada dados) {
        return relacionamento.consultarEstudante(dados.matricula());
    }

    @Override
    protected void validar(Entrada dados) {
        ValidadorDeDados.exigirIdentificadorPositivo(dados.matricula(), "matrícula");
        ValidadorDeDados.exigirCpf(dados.cpf());
    }

    @Override
    protected void verificarPreRequisitos(Entrada dados) {
        String cpf = dados.cpf().trim();
        if (relacionamento.consultarPessoa(cpf).isEmpty()) {
            throw new PreRequisitoNaoAtendidoException(
                    "Não é possível cadastrar estudante: não existe Pessoa cadastrada com CPF " + cpf + ".");
        }
    }

    @Override
    protected Estudante criar(Entrada dados) {
        return new Estudante(dados.cpf().trim(), dados.matricula());
    }

    @Override
    protected void armazenar(Estudante entidade) {
        relacionamento.inserirEstudante(entidade);
    }

    @Override
    protected DadosEstudante apresentar(Estudante entidade) {
        String nomeCompleto = relacionamento.consultarPessoa(entidade.getCpf())
                .map(Pessoa::getNomeCompleto)
                .orElse("(pessoa não localizada)");
        return new DadosEstudante(entidade.getMatricula(), entidade.getCpf(), nomeCompleto);
    }
}
