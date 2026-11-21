package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.persistencia.RepositorioDeLivros;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** <b>R08 — Abstract Factory: produto concreto "repositório de Livro" (família em memória).</b> */
public final class RepositorioDeLivrosEmMemoria implements RepositorioDeLivros, ParticipanteTransacional {

    private final UnidadeDeTrabalhoEmMemoria unidadeDeTrabalho;
    private Map<Integer, Livro> livros = new LinkedHashMap<>();
    private Map<Integer, Livro> snapshot;

    public RepositorioDeLivrosEmMemoria(UnidadeDeTrabalhoEmMemoria unidadeDeTrabalho) {
        this.unidadeDeTrabalho = Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho é obrigatória.");
        this.unidadeDeTrabalho.registrar(this);
    }

    @Override
    public UnidadeDeTrabalho unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvar(Livro livro) {
        Objects.requireNonNull(livro, "Livro não pode ser nulo.");
        livros.put(livro.getIdLivro(), copiar(livro));
    }

    @Override
    public Optional<Livro> buscar(int idLivro) {
        Livro encontrado = livros.get(idLivro);
        return encontrado == null ? Optional.empty() : Optional.of(copiar(encontrado));
    }

    @Override
    public void remover(int idLivro) {
        livros.remove(idLivro);
    }

    @Override
    public List<Livro> listar() {
        List<Livro> copia = new ArrayList<>(livros.size());
        for (Livro livro : livros.values()) {
            copia.add(copiar(livro));
        }
        return List.copyOf(copia);
    }

    @Override
    public void capturarEstado() {
        snapshot = copiarMapa(livros);
    }

    @Override
    public void confirmarEstado() {
        snapshot = null;
    }

    @Override
    public void restaurarEstado() {
        if (snapshot != null) {
            livros = snapshot;
            snapshot = null;
        }
    }

    private static Map<Integer, Livro> copiarMapa(Map<Integer, Livro> origem) {
        Map<Integer, Livro> copia = new LinkedHashMap<>(origem.size());
        for (Map.Entry<Integer, Livro> entrada : origem.entrySet()) {
            copia.put(entrada.getKey(), copiar(entrada.getValue()));
        }
        return copia;
    }

    private static Livro copiar(Livro livro) {
        return Livro.reconstituir(livro.getIdLivro(), livro.getTitulo(), livro.getEstado().nome());
    }
}
