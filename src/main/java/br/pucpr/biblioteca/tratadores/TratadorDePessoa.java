package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.cadastro.CadastroDePessoa;
import br.pucpr.biblioteca.dominio.excecao.DadosInvalidosException;
import br.pucpr.biblioteca.dominio.excecao.RegistroNaoEncontradoException;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.dto.DadosPessoa;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;
import br.pucpr.biblioteca.interfaces.iPessoa;
import br.pucpr.biblioteca.nucleo.Relacionamento;

import java.util.List;
import java.util.Objects;

/** Tratador de pessoas (componente RH). Delega a inclusão ao Template Method (R07) e demais operações a {@link Relacionamento}. */
public class TratadorDePessoa implements iPessoa {

    private final CadastroDePessoa cadastro;
    private final Relacionamento relacionamento;

    public TratadorDePessoa(CadastroDePessoa cadastro, Relacionamento relacionamento) {
        this.cadastro = Objects.requireNonNull(cadastro, "CadastroDePessoa é obrigatório.");
        this.relacionamento = Objects.requireNonNull(relacionamento, "Relacionamento é obrigatório.");
    }

    @Override
    public ResultadoDoCadastro<DadosPessoa> inserirPessoa(String nome, String sobrenome, String cpf) {
        return cadastro.incluir(new CadastroDePessoa.Entrada(nome, sobrenome, cpf));
    }

    @Override
    public DadosPessoa consultarPessoa(String cpf) {
        Pessoa pessoa = relacionamento.consultarPessoa(cpf)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Pessoa não cadastrada para CPF " + cpf + "."));
        return DadosPessoa.de(pessoa);
    }

    @Override
    public DadosPessoa atualizarPessoa(String cpf, String novoNome, String novoSobrenome) {
        Pessoa pessoa = relacionamento.consultarPessoa(cpf)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Pessoa não cadastrada para CPF " + cpf + "."));
        if (novoNome == null || novoNome.isBlank() || novoSobrenome == null || novoSobrenome.isBlank()) {
            throw new DadosInvalidosException("Nome e sobrenome são obrigatórios na atualização.");
        }
        pessoa.atualizarPessoa(novoNome.trim(), novoSobrenome.trim());
        relacionamento.atualizarPessoa(pessoa);
        return DadosPessoa.de(pessoa);
    }

    @Override
    public void excluirPessoa(String cpf) {
        if (relacionamento.consultarPessoa(cpf).isEmpty()) {
            throw new RegistroNaoEncontradoException("Pessoa não cadastrada para CPF " + cpf + ".");
        }
        relacionamento.excluirPessoa(cpf);
    }

    @Override
    public List<DadosPessoa> listarPessoas() {
        return relacionamento.listarPessoas().stream().map(DadosPessoa::de).toList();
    }
}
