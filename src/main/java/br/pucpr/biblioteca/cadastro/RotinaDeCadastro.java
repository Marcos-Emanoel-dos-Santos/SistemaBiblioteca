package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dto.ResultadoDoCadastro;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.util.Objects;
import java.util.Optional;

/**
 * <b>R07 — Template Method: papel AbstractClass.</b>
 * <p>
 * O método-modelo {@link #incluir(Object)} é {@code final} e fixa a sequência exata da rotina
 * de inclusão dentro de uma transação da {@link UnidadeDeTrabalho}:
 * <ol>
 *   <li>{@link #buscarExistente(Object)} — se já existir, encerra e apresenta o cadastro existente;</li>
 *   <li>{@link #validar(Object)} — valida os campos obrigatórios;</li>
 *   <li>{@link #verificarPreRequisitos(Object)} — <b>gancho (hook)</b>, vazio por padrão, sobrescrito
 *       por Estudante e Atendente para exigir Pessoa (CPF) previamente cadastrada;</li>
 *   <li>{@link #criar(Object)} — instancia a entidade de domínio;</li>
 *   <li>{@link #armazenar(Object)} — grava no ponto interno correspondente;</li>
 *   <li>{@link #apresentar(Object)} — converte em DTO para retorno à interface.</li>
 * </ol>
 * Se qualquer passo (incluindo o gancho de pré-requisitos) lançar exceção, a execução é interrompida
 * antes de {@code criar} e {@code armazenar}, e a transação é desfeita sem gravação parcial.
 */
public abstract class RotinaDeCadastro<D, E, R> {

    private final UnidadeDeTrabalho unidadeDeTrabalho;

    protected RotinaDeCadastro(UnidadeDeTrabalho unidadeDeTrabalho) {
        this.unidadeDeTrabalho = Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho é obrigatória.");
    }

    /** Método-modelo (fixo, não pode ser sobrescrito). */
    public final ResultadoDoCadastro<R> incluir(D dados) {
        Objects.requireNonNull(dados, "Dados do cadastro não podem ser nulos.");
        return unidadeDeTrabalho.executar(() -> {
            Optional<E> existente = buscarExistente(dados);
            if (existente.isPresent()) {
                return ResultadoDoCadastro.existente(apresentar(existente.get()));
            }
            validar(dados);
            verificarPreRequisitos(dados);
            E novaEntidade = criar(dados);
            armazenar(novaEntidade);
            return ResultadoDoCadastro.novo(apresentar(novaEntidade));
        });
    }

    protected abstract Optional<E> buscarExistente(D dados);

    protected abstract void validar(D dados);

    /**
     * Gancho (hook) opcional executado após a validação e antes da criação/armazenamento.
     * Subclasses que possuem pré-requisitos externos (Estudante e Atendente) sobrescrevem este método.
     */
    protected void verificarPreRequisitos(D dados) {
        // Sem pré-requisitos por padrão (Livro e Pessoa).
    }

    protected abstract E criar(D dados);

    protected abstract void armazenar(E entidade);

    protected abstract R apresentar(E entidade);
}
