package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.pessoa.Atendente;
import br.pucpr.biblioteca.dominio.pessoa.Estudante;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;

import java.util.List;
import java.util.Optional;

/**
 * <b>R08 — Abstract Factory: produto abstrato "repositório de Relacionamento".</b>
 * Mantém as três associações distintas do Relacionamento: pessoa, estudante e atendente.
 */
public interface RepositorioDeRelacionamento extends Repositorio {

    void salvarPessoa(Pessoa pessoa);

    Optional<Pessoa> buscarPessoa(String cpf);

    void removerPessoa(String cpf);

    List<Pessoa> listarPessoas();

    void salvarEstudante(Estudante estudante);

    Optional<Estudante> buscarEstudante(int matricula);

    void removerEstudante(int matricula);

    List<Estudante> listarEstudantes();

    void salvarAtendente(Atendente atendente);

    Optional<Atendente> buscarAtendente(int idAtendente);

    void removerAtendente(int idAtendente);

    List<Atendente> listarAtendentes();
}
