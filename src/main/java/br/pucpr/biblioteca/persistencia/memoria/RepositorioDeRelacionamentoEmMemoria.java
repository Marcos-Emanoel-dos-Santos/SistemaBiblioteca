package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.dominio.pessoa.Atendente;
import br.pucpr.biblioteca.dominio.pessoa.Estudante;
import br.pucpr.biblioteca.dominio.pessoa.Pessoa;
import br.pucpr.biblioteca.persistencia.RepositorioDeRelacionamento;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * <b>R08 — Abstract Factory: produto concreto "repositório de Relacionamento" (família em memória).</b>
 * Mantém as três associações distintas: pessoa, estudante e atendente.
 */
public final class RepositorioDeRelacionamentoEmMemoria
        implements RepositorioDeRelacionamento, ParticipanteTransacional {

    private final UnidadeDeTrabalhoEmMemoria unidadeDeTrabalho;
    private Map<String, Pessoa> pessoas = new LinkedHashMap<>();
    private Map<Integer, Estudante> estudantes = new LinkedHashMap<>();
    private Map<Integer, Atendente> atendentes = new LinkedHashMap<>();

    private Map<String, Pessoa> snapshotPessoas;
    private Map<Integer, Estudante> snapshotEstudantes;
    private Map<Integer, Atendente> snapshotAtendentes;

    public RepositorioDeRelacionamentoEmMemoria(UnidadeDeTrabalhoEmMemoria unidadeDeTrabalho) {
        this.unidadeDeTrabalho = Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho é obrigatória.");
        this.unidadeDeTrabalho.registrar(this);
    }

    @Override
    public UnidadeDeTrabalho unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvarPessoa(Pessoa pessoa) {
        Objects.requireNonNull(pessoa, "Pessoa não pode ser nula.");
        pessoas.put(pessoa.getCpf(), copiar(pessoa));
    }

    @Override
    public Optional<Pessoa> buscarPessoa(String cpf) {
        if (cpf == null) {
            return Optional.empty();
        }
        Pessoa encontrada = pessoas.get(cpf);
        return encontrada == null ? Optional.empty() : Optional.of(copiar(encontrada));
    }

    @Override
    public void removerPessoa(String cpf) {
        pessoas.remove(cpf);
    }

    @Override
    public List<Pessoa> listarPessoas() {
        List<Pessoa> copia = new ArrayList<>(pessoas.size());
        for (Pessoa pessoa : pessoas.values()) {
            copia.add(copiar(pessoa));
        }
        return List.copyOf(copia);
    }

    @Override
    public void salvarEstudante(Estudante estudante) {
        Objects.requireNonNull(estudante, "Estudante não pode ser nulo.");
        estudantes.put(estudante.getMatricula(), copiar(estudante));
    }

    @Override
    public Optional<Estudante> buscarEstudante(int matricula) {
        Estudante encontrado = estudantes.get(matricula);
        return encontrado == null ? Optional.empty() : Optional.of(copiar(encontrado));
    }

    @Override
    public void removerEstudante(int matricula) {
        estudantes.remove(matricula);
    }

    @Override
    public List<Estudante> listarEstudantes() {
        List<Estudante> copia = new ArrayList<>(estudantes.size());
        for (Estudante estudante : estudantes.values()) {
            copia.add(copiar(estudante));
        }
        return List.copyOf(copia);
    }

    @Override
    public void salvarAtendente(Atendente atendente) {
        Objects.requireNonNull(atendente, "Atendente não pode ser nulo.");
        atendentes.put(atendente.getIdAtendente(), copiar(atendente));
    }

    @Override
    public Optional<Atendente> buscarAtendente(int idAtendente) {
        Atendente encontrado = atendentes.get(idAtendente);
        return encontrado == null ? Optional.empty() : Optional.of(copiar(encontrado));
    }

    @Override
    public void removerAtendente(int idAtendente) {
        atendentes.remove(idAtendente);
    }

    @Override
    public List<Atendente> listarAtendentes() {
        List<Atendente> copia = new ArrayList<>(atendentes.size());
        for (Atendente atendente : atendentes.values()) {
            copia.add(copiar(atendente));
        }
        return List.copyOf(copia);
    }

    @Override
    public void capturarEstado() {
        snapshotPessoas = new LinkedHashMap<>();
        for (Map.Entry<String, Pessoa> entry : pessoas.entrySet()) {
            snapshotPessoas.put(entry.getKey(), copiar(entry.getValue()));
        }
        snapshotEstudantes = new LinkedHashMap<>();
        for (Map.Entry<Integer, Estudante> entry : estudantes.entrySet()) {
            snapshotEstudantes.put(entry.getKey(), copiar(entry.getValue()));
        }
        snapshotAtendentes = new LinkedHashMap<>();
        for (Map.Entry<Integer, Atendente> entry : atendentes.entrySet()) {
            snapshotAtendentes.put(entry.getKey(), copiar(entry.getValue()));
        }
    }

    @Override
    public void confirmarEstado() {
        snapshotPessoas = null;
        snapshotEstudantes = null;
        snapshotAtendentes = null;
    }

    @Override
    public void restaurarEstado() {
        if (snapshotPessoas != null) {
            pessoas = snapshotPessoas;
            estudantes = snapshotEstudantes;
            atendentes = snapshotAtendentes;
            snapshotPessoas = null;
            snapshotEstudantes = null;
            snapshotAtendentes = null;
        }
    }

    private static Pessoa copiar(Pessoa p) {
        return new Pessoa(p.getNome(), p.getSobrenome(), p.getCpf());
    }

    private static Estudante copiar(Estudante e) {
        return new Estudante(e.getCpf(), e.getMatricula());
    }

    private static Atendente copiar(Atendente a) {
        return new Atendente(a.getCpf(), a.getIdAtendente());
    }
}
