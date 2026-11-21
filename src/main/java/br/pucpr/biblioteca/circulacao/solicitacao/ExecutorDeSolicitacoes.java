package br.pucpr.biblioteca.circulacao.solicitacao;

import br.pucpr.biblioteca.interfaces.iCirculacao;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <b>R06 — Command: papel Invoker (com controle de idempotência seguro para concorrência).</b>
 * <p>
 * Garante que uma solicitação com o mesmo identificador seja executada no máximo uma vez, mesmo
 * sob submissões simultâneas em múltiplas threads: usa {@link ConcurrentHashMap#putIfAbsent} com
 * um {@link CompletableFuture} por identificador. A primeira submissão executa o comando no
 * domínio e publica o resultado; submissões concorrentes ou repetidas recebem o mesmo resultado
 * sem reaplicar a operação.
 */
public final class ExecutorDeSolicitacoes {

    public record RegistroDeExecucao(String idSolicitacao, String tipo, String autor,
                                     java.time.Instant instanteCriacao) {
    }

    private final iCirculacao circulacao;
    private final ConcurrentHashMap<String, CompletableFuture<Object>> resultadosPorId = new ConcurrentHashMap<>();
    private final List<RegistroDeExecucao> historico = new CopyOnWriteArrayList<>();

    public ExecutorDeSolicitacoes(iCirculacao circulacao) {
        this.circulacao = Objects.requireNonNull(circulacao, "Contrato de circulação é obrigatório.");
    }

    @SuppressWarnings("unchecked")
    public <R> R executar(SolicitacaoDeCirculacao<R> solicitacao) {
        Objects.requireNonNull(solicitacao, "Solicitação não pode ser nula.");
        String id = solicitacao.getId();

        CompletableFuture<Object> novo = new CompletableFuture<>();
        CompletableFuture<Object> existente = resultadosPorId.putIfAbsent(id, novo);

        if (existente != null) {
            return (R) aguardarResultado(existente);
        }

        try {
            R resultado = solicitacao.executar(circulacao);
            historico.add(new RegistroDeExecucao(
                    id, solicitacao.getClass().getSimpleName(),
                    solicitacao.getAutor(), solicitacao.getInstanteCriacao()));
            novo.complete(resultado);
            return resultado;
        } catch (RuntimeException falha) {
            novo.completeExceptionally(falha);
            resultadosPorId.remove(id, novo);
            throw falha;
        }
    }

    public boolean jaExecutada(String idSolicitacao) {
        CompletableFuture<Object> future = resultadosPorId.get(idSolicitacao);
        return future != null && future.isDone() && !future.isCompletedExceptionally();
    }

    public List<RegistroDeExecucao> listarHistorico() {
        return Collections.unmodifiableList(historico);
    }

    private static Object aguardarResultado(CompletableFuture<Object> future) {
        try {
            return future.join();
        } catch (CompletionException ce) {
            if (ce.getCause() instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw ce;
        }
    }
}
