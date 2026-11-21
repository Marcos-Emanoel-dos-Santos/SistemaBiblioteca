package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.circulacao.devolucao.EventoDevolucao;
import br.pucpr.biblioteca.circulacao.devolucao.PublicadorDeDevolucao;
import br.pucpr.biblioteca.dominio.excecao.RegistroNaoEncontradoException;
import br.pucpr.biblioteca.dominio.transacao.Devolucao;
import br.pucpr.biblioteca.dto.ComprovanteDeDevolucao;
import br.pucpr.biblioteca.dto.ComprovanteDeMulta;
import br.pucpr.biblioteca.interfaces.iCirculacao;
import br.pucpr.biblioteca.interfaces.iDevolucao;
import br.pucpr.biblioteca.interfaces.iMulta;
import br.pucpr.biblioteca.nucleo.Movimentacao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Tratador dos serviços do Atendente (f5–f6 — implementa {@link iDevolucao}).
 * <p>
 * Delega o registro transacional da devolução ao contrato {@link iCirculacao} (R04 — Facade) e,
 * somente <b>após</b> a confirmação, publica o {@link EventoDevolucao} no
 * {@link PublicadorDeDevolucao} (R05 — Observer). Para f6 (segunda via de boleto), delega a
 * {@link iMulta}.
 */
public class TratadorDeDevolucao implements iDevolucao {

    private final iCirculacao circulacao;
    private final PublicadorDeDevolucao publicador;
    private final Movimentacao movimentacao;
    private final iMulta servicoDeMultas;

    public TratadorDeDevolucao(iCirculacao circulacao,
                               PublicadorDeDevolucao publicador,
                               Movimentacao movimentacao,
                               iMulta servicoDeMultas) {
        this.circulacao = Objects.requireNonNull(circulacao, "Contrato de circulação é obrigatório.");
        this.publicador = Objects.requireNonNull(publicador, "Publicador de devolução é obrigatório.");
        this.movimentacao = Objects.requireNonNull(movimentacao, "Movimentacao é obrigatória.");
        this.servicoDeMultas = Objects.requireNonNull(servicoDeMultas, "Serviço de multas é obrigatório.");
    }

    @Override
    public ComprovanteDeDevolucao registrarDevolucao(int idLivro, int matricula) {
        ComprovanteDeDevolucao comprovante = circulacao.registrarDevolucao(idLivro, matricula);
        // Publicação ocorre estritamente após a confirmação transacional da devolução (R05)
        publicador.publicar(EventoDevolucao.de(comprovante));
        return comprovante;
    }

    @Override
    public ComprovanteDeMulta emitirBoletoMulta(int idLivro, int matricula) {
        Devolucao devolucao = movimentacao.consultarDevolucao(idLivro, matricula)
                .orElseThrow(() -> new RegistroNaoEncontradoException(
                        "Devolução não registrada para livro " + idLivro + " e estudante " + matricula + "."));
        return servicoDeMultas.emitirSegundaVia(devolucao.getId());
    }

    @Override
    public Optional<ComprovanteDeMulta> consultarMultaPorDevolucao(String idDevolucao) {
        return servicoDeMultas.consultarPorDevolucao(idDevolucao);
    }

    @Override
    public List<ComprovanteDeMulta> listarMultas() {
        return servicoDeMultas.listarMultas();
    }
}
