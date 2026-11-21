package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.cadastro.CadastroDeAtendente;
import br.pucpr.biblioteca.dominio.excecao.PreRequisitoNaoAtendidoException;
import br.pucpr.biblioteca.dominio.excecao.RegistroNaoEncontradoException;
import br.pucpr.biblioteca.dominio.pessoa.Atendente;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.dto.DadosAtendente;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;
import br.pucpr.biblioteca.interfaces.iAtendente;
import br.pucpr.biblioteca.nucleo.Relacionamento;

import java.util.List;
import java.util.Objects;

/** Tratador de atendentes (componente RH). Delega a inclusão ao Template Method (R07) e demais operações a {@link Relacionamento}. */
public class TratadorDeAtendente implements iAtendente {

    private final CadastroDeAtendente cadastro;
    private final Relacionamento relacionamento;

    public TratadorDeAtendente(CadastroDeAtendente cadastro, Relacionamento relacionamento) {
        this.cadastro = Objects.requireNonNull(cadastro, "CadastroDeAtendente é obrigatório.");
        this.relacionamento = Objects.requireNonNull(relacionamento, "Relacionamento é obrigatório.");
    }

    @Override
    public ResultadoDoCadastro<DadosAtendente> inserirAtendente(String cpf, int idAtendente) {
        return cadastro.incluir(new CadastroDeAtendente.Entrada(cpf, idAtendente));
    }

    @Override
    public DadosAtendente consultarAtendente(int idAtendente) {
        Atendente atendente = relacionamento.consultarAtendente(idAtendente)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Atendente não cadastrado: id " + idAtendente + "."));
        return apresentar(atendente);
    }

    @Override
    public DadosAtendente atualizarAtendente(int idAtendente, String novoCpf) {
        Atendente atendente = relacionamento.consultarAtendente(idAtendente)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Atendente não cadastrado: id " + idAtendente + "."));
        if (novoCpf == null || relacionamento.consultarPessoa(novoCpf.trim()).isEmpty()) {
            throw new PreRequisitoNaoAtendidoException("Pessoa não cadastrada para CPF " + novoCpf + ".");
        }
        atendente.atualizarAtendente(novoCpf.trim());
        relacionamento.atualizarAtendente(atendente);
        return apresentar(atendente);
    }

    @Override
    public void excluirAtendente(int idAtendente) {
        if (relacionamento.consultarAtendente(idAtendente).isEmpty()) {
            throw new RegistroNaoEncontradoException("Atendente não cadastrado: id " + idAtendente + ".");
        }
        relacionamento.excluirAtendente(idAtendente);
    }

    @Override
    public List<DadosAtendente> listarAtendentes() {
        return relacionamento.listarAtendentes().stream().map(this::apresentar).toList();
    }

    private DadosAtendente apresentar(Atendente atendente) {
        String nomeCompleto = relacionamento.consultarPessoa(atendente.getCpf())
                .map(Pessoa::getNomeCompleto)
                .orElse("(pessoa não localizada)");
        return new DadosAtendente(atendente.getIdAtendente(), atendente.getCpf(), nomeCompleto);
    }
}
