package br.pucpr.biblioteca.organizacao;

import br.pucpr.biblioteca.dominio.excecao.DadosInvalidosException;
import br.pucpr.biblioteca.dominio.excecao.RegraDeNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * <b>R10 — Composite: papel Composite.</b>
 * <p>
 * Representa unidades, seções ou subseções de profundidade variável. Garante duas invariantes
 * estruturais:
 * <ul>
 *   <li><b>Sem ciclos:</b> impede adicionar o próprio grupo ou qualquer ancestral seu como filho;</li>
 *   <li><b>Unicidade de alocação:</b> impede que um mesmo livro pertença a dois grupos na árvore.</li>
 * </ul>
 */
public final class GrupoDoAcervo implements ComponenteDoAcervo {

    private final String nome;
    private final TipoDeGrupo tipo;
    private GrupoDoAcervo pai;
    private final List<ComponenteDoAcervo> filhos = new ArrayList<>();

    public GrupoDoAcervo(String nome, TipoDeGrupo tipo) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("Nome do grupo do acervo é obrigatório.");
        }
        this.nome = nome.trim();
        this.tipo = Objects.requireNonNull(tipo, "Tipo de grupo é obrigatório.");
    }

    public TipoDeGrupo getTipo() {
        return tipo;
    }

    public GrupoDoAcervo getPai() {
        return pai;
    }

    public List<ComponenteDoAcervo> getFilhos() {
        return Collections.unmodifiableList(filhos);
    }

    public void adicionar(ComponenteDoAcervo componente) {
        Objects.requireNonNull(componente, "Componente não pode ser nulo.");

        if (componente instanceof GrupoDoAcervo subgrupo) {
            if (subgrupo == this || ehAncestral(subgrupo)) {
                throw new RegraDeNegocioException(
                        "Operação recusada: adicionar '" + subgrupo.getNome() + "' em '" + nome + "' criaria um ciclo.");
            }
            if (subgrupo.pai != null) {
                throw new RegraDeNegocioException(
                        "Grupo '" + subgrupo.getNome() + "' já pertence ao grupo '" + subgrupo.pai.getNome() + "'.");
            }
            Set<Integer> existentesNaArvore = new HashSet<>(raiz().listarIdsLivros());
            for (Integer idLivro : subgrupo.listarIdsLivros()) {
                if (existentesNaArvore.contains(idLivro)) {
                    throw new RegraDeNegocioException(
                            "Livro " + idLivro + " já pertence a outro grupo nesta hierarquia.");
                }
            }
            subgrupo.pai = this;
            filhos.add(subgrupo);
            return;
        }

        if (componente instanceof ItemDeLivro item) {
            if (raiz().listarIdsLivros().contains(item.getIdLivro())) {
                throw new RegraDeNegocioException(
                        "Livro " + item.getIdLivro() + " já pertence a outro grupo do acervo.");
            }
            filhos.add(item);
            return;
        }

        filhos.add(componente);
    }

    public boolean remover(ComponenteDoAcervo componente) {
        boolean removido = filhos.remove(componente);
        if (removido && componente instanceof GrupoDoAcervo subgrupo) {
            subgrupo.pai = null;
        }
        return removido;
    }

    public Optional<ItemDeLivro> removerItemDireto(int idLivro) {
        for (int i = 0; i < filhos.size(); i++) {
            ComponenteDoAcervo filho = filhos.get(i);
            if (filho instanceof ItemDeLivro item && item.getIdLivro() == idLivro) {
                filhos.remove(i);
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public int contarLivros() {
        int total = 0;
        for (ComponenteDoAcervo filho : filhos) {
            total += filho.contarLivros();
        }
        return total;
    }

    @Override
    public List<Integer> listarIdsLivros() {
        List<Integer> ids = new ArrayList<>();
        for (ComponenteDoAcervo filho : filhos) {
            ids.addAll(filho.listarIdsLivros());
        }
        return List.copyOf(ids);
    }

    private boolean ehAncestral(GrupoDoAcervo candidato) {
        GrupoDoAcervo atual = this.pai;
        while (atual != null) {
            if (atual == candidato) {
                return true;
            }
            atual = atual.pai;
        }
        return false;
    }

    private GrupoDoAcervo raiz() {
        GrupoDoAcervo atual = this;
        while (atual.pai != null) {
            atual = atual.pai;
        }
        return atual;
    }

    @Override
    public String toString() {
        return tipo + ": " + nome;
    }
}
