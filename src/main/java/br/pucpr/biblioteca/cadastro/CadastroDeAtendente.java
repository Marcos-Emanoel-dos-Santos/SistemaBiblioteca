package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dominio.excecao.PreRequisitoNaoAtendidoException;
import br.pucpr.biblioteca.dominio.pessoa.Atendente;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.dto.DadosAtendente;
import br.pucpr.biblioteca.nucleo.Relacionamento;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.Objects;
import java.util.Optional;

/**
 * <b>R07 — Template Method: ConcreteClass para inclusão de {@link Atendente}.</b>
 * Sobrescreve o gancho {@link #verificarPreRequisitos(Entrada)} para exigir que o CPF informado
 * pertença a uma {@link Pessoa} já cadastrada no RH antes de qualquer criação ou gravação.
 */
public final class CadastroDeAtendente
        extends RotinaDeCadastro<CadastroDeAtendente.Entrada, Atendente, DadosAtendente> {

    public record Entrada(String cpf, int idAtendente) {
    }

    private final Relacionamento relacionamento;

    public CadastroDeAtendente(UnidadeDeTrabalho unidadeDeTrabalho, Relacionamento relacionamento) {
        super(unidadeDeTrabalho);
        this.relacionamento = Objects.requireNonNull(relacionamento, "Relacionamento é obrigatório.");
    }

    @Override
    protected Optional<Atendente> buscarExistente(Entrada dados) {
        return relacionamento.consultarAtendente(dados.idAtendente());
    }

    @Override
    protected void validar(Entrada dados) {
        ValidadorDeDados.exigirIdentificadorPositivo(dados.idAtendente(), "idAtendente");
        ValidadorDeDados.exigirCpf(dados.cpf());
    }

    @Override
    protected void verificarPreRequisitos(Entrada dados) {
        String cpf = dados.cpf().trim();
        if (relacionamento.consultarPessoa(cpf).isEmpty()) {
            throw new PreRequisitoNaoAtendidoException(
                    "Não é possível cadastrar atendente: não existe Pessoa cadastrada com CPF " + cpf + ".");
        }
    }

    @Override
    protected Atendente criar(Entrada dados) {
        return new Atendente(dados.cpf().trim(), dados.idAtendente());
    }

    @Override
    protected void armazenar(Atendente entidade) {
        relacionamento.inserirAtendente(entidade);
    }

    @Override
    protected DadosAtendente apresentar(Atendente entidade) {
        String nomeCompleto = relacionamento.consultarPessoa(entidade.getCpf())
                .map(Pessoa::getNomeCompleto)
                .orElse("(pessoa não localizada)");
        return new DadosAtendente(entidade.getIdAtendente(), entidade.getCpf(), nomeCompleto);
    }
}
