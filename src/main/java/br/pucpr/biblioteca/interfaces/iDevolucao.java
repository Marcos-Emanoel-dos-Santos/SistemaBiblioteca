package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.dto.ComprovanteDeDevolucao;
import br.pucpr.biblioteca.dto.ComprovanteDeMulta;

import java.util.List;
import java.util.Optional;

/**
 * Contrato do componente Biblioteca para os serviços do Atendente (f5–f6).
 * Implementado por {@code TratadorDeDevolucao}.
 */
public interface iDevolucao {

    /** f5: registrar devolução de livro (e publicar evento aos ouvintes após confirmação). */
    ComprovanteDeDevolucao registrarDevolucao(int idLivro, int matricula);

    /** f6: emitir segunda via do boleto de multa de uma devolução em atraso. */
    ComprovanteDeMulta emitirBoletoMulta(int idLivro, int matricula);

    Optional<ComprovanteDeMulta> consultarMultaPorDevolucao(String idDevolucao);

    List<ComprovanteDeMulta> listarMultas();
}
