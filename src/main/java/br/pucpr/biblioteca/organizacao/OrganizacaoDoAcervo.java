package br.pucpr.biblioteca.organizacao;

import br.pucpr.biblioteca.dominio.excecao.RegraDeNegocioException;
import br.pucpr.biblioteca.dominio.excecao.RegistroNaoEncontradoException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Coordena a árvore hierárquica do acervo (R10 — Composite), mantendo o índice global
 * {@code idLivro -> GrupoDoAcervo} para garantir que um livro não pertença a dois grupos
 * mesmo entre unidades distintas, e permitindo mover exemplares sem tocar em {@code Livro}
 * nem em {@code Movimentacao}.
 */
public final class OrganizacaoDoAcervo {

    private final Map<Integer, GrupoDoAcervo> grupoPorLivro = new HashMap<>();

    public void adicionarSubgrupo(GrupoDoAcervo pai, GrupoDoAcervo filho) {
        Objects.requireNonNull(pai, "Grupo pai é obrigatório.");
        Objects.requireNonNull(filho, "Subgrupo é obrigatório.");
        pai.adicionar(filho);
        registrarLivrosDoGrupo(filho);
    }

    public ItemDeLivro adicionarLivro(GrupoDoAcervo grupo, int idLivro) {
        Objects.requireNonNull(grupo, "Grupo de destino é obrigatório.");
        GrupoDoAcervo atual = grupoPorLivro.get(idLivro);
        if (atual != null) {
            throw new RegraDeNegocioException(
                    "Livro " + idLivro + " já pertence ao grupo '" + atual.getNome() + "'.");
        }
        ItemDeLivro item = new ItemDeLivro(idLivro);
        grupo.adicionar(item);
        grupoPorLivro.put(idLivro, grupo);
        return item;
    }

    /**
     * Move o item de um grupo para outro mexendo apenas nos nós {@link GrupoDoAcervo}/{@link ItemDeLivro}.
     * Não consulta nem altera {@code Livro} ou {@code Movimentacao}.
     */
    public void moverLivro(int idLivro, GrupoDoAcervo destino) {
        Objects.requireNonNull(destino, "Grupo de destino é obrigatório.");
        GrupoDoAcervo origem = grupoPorLivro.get(idLivro);
        if (origem == null) {
            throw new RegistroNaoEncontradoException("Livro " + idLivro + " não está alocado em nenhum grupo.");
        }
        if (origem == destino) {
            return;
        }
        ItemDeLivro item = origem.removerItemDireto(idLivro)
                .orElseThrow(() -> new RegistroNaoEncontradoException(
                        "Item do livro " + idLivro + " não encontrado no grupo '" + origem.getNome() + "'."));
        try {
            destino.adicionar(item);
            grupoPorLivro.put(idLivro, destino);
        } catch (RuntimeException falha) {
            // Reverte alocação se destino recusar
            origem.adicionar(item);
            throw falha;
        }
    }

    public Optional<GrupoDoAcervo> localizarGrupoDoLivro(int idLivro) {
        return Optional.ofNullable(grupoPorLivro.get(idLivro));
    }

    private void registrarLivrosDoGrupo(GrupoDoAcervo grupo) {
        for (ComponenteDoAcervo filho : grupo.getFilhos()) {
            if (filho instanceof ItemDeLivro item) {
                GrupoDoAcervo existente = grupoPorLivro.get(item.getIdLivro());
                if (existente != null && existente != grupo) {
                    throw new RegraDeNegocioException(
                            "Livro " + item.getIdLivro() + " já pertence ao grupo '" + existente.getNome() + "'.");
                }
                grupoPorLivro.put(item.getIdLivro(), grupo);
            } else if (filho instanceof GrupoDoAcervo sub) {
                registrarLivrosDoGrupo(sub);
            }
        }
    }
}
