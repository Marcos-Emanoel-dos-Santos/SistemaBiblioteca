package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.cadastro.CadastroDeEstudante;
import br.pucpr.biblioteca.dominio.excecao.PreRequisitoNaoAtendidoException;
import br.pucpr.biblioteca.dominio.excecao.RegistroNaoEncontradoException;
import br.pucpr.biblioteca.dominio.pessoa.Estudante;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.dto.DadosEstudante;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;
import br.pucpr.biblioteca.interfaces.iEstudante;
import br.pucpr.biblioteca.nucleo.Relacionamento;

import java.util.List;
import java.util.Objects;

/** Tratador de estudantes (componente Acadêmico). Delega a inclusão ao Template Method (R07) e demais operações a {@link Relacionamento}. */
public class TratadorDeEstudante implements iEstudante {

    private final CadastroDeEstudante cadastro;
    private final Relacionamento relacionamento;

    public TratadorDeEstudante(CadastroDeEstudante cadastro, Relacionamento relacionamento) {
        this.cadastro = Objects.requireNonNull(cadastro, "CadastroDeEstudante é obrigatório.");
        this.relacionamento = Objects.requireNonNull(relacionamento, "Relacionamento é obrigatório.");
    }

    @Override
    public ResultadoDoCadastro<DadosEstudante> inserirEstudante(String cpf, int matricula) {
        return cadastro.incluir(new CadastroDeEstudante.Entrada(cpf, matricula));
    }

    @Override
    public DadosEstudante consultarEstudante(int matricula) {
        Estudante estudante = relacionamento.consultarEstudante(matricula)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Estudante não cadastrado: matrícula " + matricula + "."));
        return apresentar(estudante);
    }

    @Override
    public DadosEstudante atualizarEstudante(int matricula, String novoCpf) {
        Estudante estudante = relacionamento.consultarEstudante(matricula)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Estudante não cadastrado: matrícula " + matricula + "."));
        if (novoCpf == null || relacionamento.consultarPessoa(novoCpf.trim()).isEmpty()) {
            throw new PreRequisitoNaoAtendidoException("Pessoa não cadastrada para CPF " + novoCpf + ".");
        }
        estudante.atualizarEstudante(novoCpf.trim());
        relacionamento.atualizarEstudante(estudante);
        return apresentar(estudante);
    }

    @Override
    public void excluirEstudante(int matricula) {
        if (relacionamento.consultarEstudante(matricula).isEmpty()) {
            throw new RegistroNaoEncontradoException("Estudante não cadastrado: matrícula " + matricula + ".");
        }
        relacionamento.excluirEstudante(matricula);
    }

    @Override
    public List<DadosEstudante> listarEstudantes() {
        return relacionamento.listarEstudantes().stream().map(this::apresentar).toList();
    }

    private DadosEstudante apresentar(Estudante estudante) {
        String nomeCompleto = relacionamento.consultarPessoa(estudante.getCpf())
                .map(Pessoa::getNomeCompleto)
                .orElse("(pessoa não localizada)");
        return new DadosEstudante(estudante.getMatricula(), estudante.getCpf(), nomeCompleto);
    }
}
