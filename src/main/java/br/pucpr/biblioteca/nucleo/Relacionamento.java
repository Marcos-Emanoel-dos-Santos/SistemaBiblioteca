package br.pucpr.biblioteca.nucleo;

import br.pucpr.biblioteca.dominio.pessoa.Atendente;
import br.pucpr.biblioteca.dominio.pessoa.Estudante;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.persistencia.RepositorioDeRelacionamento;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Ponto interno de acesso <b>Relacionamento</b> (Trabalho 02).
 * Mantém as associações distintas de {@link Pessoa}, {@link Estudante} e {@link Atendente},
 * delegando a persistência ao {@link RepositorioDeRelacionamento} da família ativa (R08).
 */
public class Relacionamento {

    private final RepositorioDeRelacionamento repositorio;

    public Relacionamento(RepositorioDeRelacionamento repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "Repositório de relacionamento é obrigatório.");
    }

    // --- Associação Pessoa ---

    public Optional<Pessoa> consultarPessoa(String cpf) {
        return repositorio.buscarPessoa(cpf);
    }

    public void inserirPessoa(Pessoa pessoa) {
        repositorio.salvarPessoa(pessoa);
    }

    public void atualizarPessoa(Pessoa pessoa) {
        repositorio.salvarPessoa(pessoa);
    }

    public void excluirPessoa(String cpf) {
        repositorio.removerPessoa(cpf);
    }

    public List<Pessoa> listarPessoas() {
        return repositorio.listarPessoas();
    }

    // --- Associação Estudante ---

    public Optional<Estudante> consultarEstudante(int matricula) {
        return repositorio.buscarEstudante(matricula);
    }

    public void inserirEstudante(Estudante estudante) {
        repositorio.salvarEstudante(estudante);
    }

    public void atualizarEstudante(Estudante estudante) {
        repositorio.salvarEstudante(estudante);
    }

    public void excluirEstudante(int matricula) {
        repositorio.removerEstudante(matricula);
    }

    public List<Estudante> listarEstudantes() {
        return repositorio.listarEstudantes();
    }

    // --- Associação Atendente ---

    public Optional<Atendente> consultarAtendente(int idAtendente) {
        return repositorio.buscarAtendente(idAtendente);
    }

    public void inserirAtendente(Atendente atendente) {
        repositorio.salvarAtendente(atendente);
    }

    public void atualizarAtendente(Atendente atendente) {
        repositorio.salvarAtendente(atendente);
    }

    public void excluirAtendente(int idAtendente) {
        repositorio.removerAtendente(idAtendente);
    }

    public List<Atendente> listarAtendentes() {
        return repositorio.listarAtendentes();
    }
}
