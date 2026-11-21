package br.pucpr.biblioteca.consulta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * <b>R09 — Decorator: ConcreteDecorator de cache de metadados.</b>
 * <p>
 * Armazena apenas {@link MetadadosLivro} (id e título, nunca o estado do livro).
 * É resiliente a falhas: se o armazenamento de cache falhar na leitura ou gravação, recorre
 * automaticamente ao serviço delegado sem quebrar a consulta.
 */
public final class ConsultaComCache extends DecoradorDeConsulta {

    /** Abstração simples do armazenamento de cache (permite simular falha em testes). */
    public interface ArmazenamentoDeCache {
        MetadadosLivro buscar(int idLivro);

        void salvar(int idLivro, MetadadosLivro metadados);

        void invalidar(int idLivro);

        void limpar();
    }

    public static final class CacheEmMemoria implements ArmazenamentoDeCache {
        private final Map<Integer, MetadadosLivro> entradas = new HashMap<>();

        @Override
        public MetadadosLivro buscar(int idLivro) {
            return entradas.get(idLivro);
        }

        @Override
        public void salvar(int idLivro, MetadadosLivro metadados) {
            entradas.put(idLivro, metadados);
        }

        @Override
        public void invalidar(int idLivro) {
            entradas.remove(idLivro);
        }

        @Override
        public void limpar() {
            entradas.clear();
        }
    }

    private final ArmazenamentoDeCache cache;
    private int acertosNoCache;
    private int falhasDeCacheIgnoradas;

    public ConsultaComCache(ServicoDeConsultaAoAcervo delegado) {
        this(delegado, new CacheEmMemoria());
    }

    public ConsultaComCache(ServicoDeConsultaAoAcervo delegado, ArmazenamentoDeCache cache) {
        super(delegado);
        this.cache = Objects.requireNonNull(cache, "Armazenamento de cache é obrigatório.");
    }

    @Override
    public Optional<MetadadosLivro> consultarMetadados(int idLivro) {
        try {
            MetadadosLivro emCache = cache.buscar(idLivro);
            if (emCache != null) {
                acertosNoCache++;
                return Optional.of(emCache);
            }
        } catch (RuntimeException falhaLeitura) {
            falhasDeCacheIgnoradas++;
        }

        Optional<MetadadosLivro> resultado = delegado.consultarMetadados(idLivro);
        resultado.ifPresent(metadados -> {
            try {
                cache.salvar(idLivro, metadados);
            } catch (RuntimeException falhaEscrita) {
                falhasDeCacheIgnoradas++;
            }
        });
        return resultado;
    }

    public void invalidar(int idLivro) {
        try {
            cache.invalidar(idLivro);
        } catch (RuntimeException falha) {
            falhasDeCacheIgnoradas++;
        }
    }

    public int getAcertosNoCache() {
        return acertosNoCache;
    }

    public int getFalhasDeCacheIgnoradas() {
        return falhasDeCacheIgnoradas;
    }
}
