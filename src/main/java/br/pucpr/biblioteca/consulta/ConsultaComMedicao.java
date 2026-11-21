package br.pucpr.biblioteca.consulta;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <b>R09 — Decorator: ConcreteDecorator de medição de tempo de resposta.</b>
 * Quando posicionado externamente a {@link ConsultaComCache} ({@code Medicao(Cache(Original))}),
 * mede inclusive consultas atendidas pelo cache.
 */
public final class ConsultaComMedicao extends DecoradorDeConsulta {

    private final RegistroDeMedicoes registro;

    public ConsultaComMedicao(ServicoDeConsultaAoAcervo delegado, RegistroDeMedicoes registro) {
        super(delegado);
        this.registro = Objects.requireNonNull(registro, "Registro de medições é obrigatório.");
    }

    @Override
    public Optional<MetadadosLivro> consultarMetadados(int idLivro) {
        long inicio = System.nanoTime();
        try {
            return delegado.consultarMetadados(idLivro);
        } finally {
            registro.registrar("consultarMetadados(" + idLivro + ")", System.nanoTime() - inicio);
        }
    }

    @Override
    public List<MetadadosLivro> listarMetadados() {
        long inicio = System.nanoTime();
        try {
            return delegado.listarMetadados();
        } finally {
            registro.registrar("listarMetadados()", System.nanoTime() - inicio);
        }
    }
}
