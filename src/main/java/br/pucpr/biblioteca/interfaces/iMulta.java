package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.dto.ComprovanteDeMulta;

import java.util.List;
import java.util.Optional;

/**
 * Contrato fornecido pelo componente Financeiro para emissão e consulta de multas (Trabalho 02 / R02).
 */
public interface iMulta {

    ComprovanteDeMulta solicitarMulta(String cpfEstudante, String idDevolucao, long diasDeAtraso);

    /** Regra 10.2 (f6): emite segunda via do boleto de uma multa já registrada sem recalcular seu valor. */
    ComprovanteDeMulta emitirSegundaVia(String idDevolucao);

    Optional<ComprovanteDeMulta> consultarPorDevolucao(String idDevolucao);

    List<ComprovanteDeMulta> listarMultas();
}
