package br.pucpr.biblioteca.financeiro;

import br.pucpr.biblioteca.dominio.excecao.RegistroNaoEncontradoException;
import br.pucpr.biblioteca.dto.ComprovanteDeMulta;
import br.pucpr.biblioteca.financeiro.banco.CobrancaEmitida;
import br.pucpr.biblioteca.financeiro.banco.NotificacaoDePagamento;
import br.pucpr.biblioteca.financeiro.banco.SolicitacaoDeCobranca;
import br.pucpr.biblioteca.financeiro.politica.PoliticaDeMulta;
import br.pucpr.biblioteca.interfaces.iCobranca;
import br.pucpr.biblioteca.interfaces.iMulta;
import br.pucpr.biblioteca.interfaces.iQuitacao;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>R02 — Strategy (papel Context) e R03 — Adapter (papel Client).</b>
 * <p>
 * Calcula a multa delegando à {@link PoliticaDeMulta} vigente na {@link ConfiguracaoAdministrativa}
 * e emite boletos exclusivamente pelo contrato institucional {@link iCobranca}, sem nenhuma
 * condicional por banco. Também processa quitações de forma idempotente via {@link iQuitacao}.
 */
public final class ServicoDeMultas implements iMulta, iQuitacao {

    private static final int DIAS_VENCIMENTO_BOLETO = 5;

    private final ConfiguracaoAdministrativa configuracao;
    private final iCobranca cobranca;
    private final Clock relogio;
    private final AtomicInteger sequencia = new AtomicInteger(1);

    private final Map<String, Multa> multasPorDevolucao = new LinkedHashMap<>();
    private final Map<String, Multa> multasPorCobranca = new LinkedHashMap<>();
    private int totalQuitacoesRegistradas;

    public ServicoDeMultas(ConfiguracaoAdministrativa configuracao, iCobranca cobranca, Clock relogio) {
        this.configuracao = Objects.requireNonNull(configuracao, "Configuração administrativa é obrigatória.");
        this.cobranca = Objects.requireNonNull(cobranca, "Serviço de cobrança é obrigatório.");
        this.relogio = Objects.requireNonNull(relogio, "Relógio é obrigatório.");
    }

    @Override
    public ComprovanteDeMulta solicitarMulta(String cpfEstudante, String idDevolucao, long diasDeAtraso) {
        Objects.requireNonNull(cpfEstudante, "CPF do estudante é obrigatório.");
        Objects.requireNonNull(idDevolucao, "Identificador da devolução é obrigatório.");

        // Se já foi emitida multa para esta devolução, devolve o registro gravado sem recalcular
        Multa existente = multasPorDevolucao.get(idDevolucao);
        if (existente != null) {
            return existente.paraComprovante();
        }

        PoliticaDeMulta politica = configuracao.getPoliticaVigente();
        BigDecimal valor = politica.calcular(diasDeAtraso);
        LocalDate hoje = LocalDate.now(relogio);
        String idMulta = "MULTA-" + sequencia.getAndIncrement();

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            Multa isenta = new Multa(
                    idMulta, idDevolucao, "ISENTA-" + idMulta, cpfEstudante,
                    diasDeAtraso, valor, politica.nome(), "ISENTO", hoje);
            multasPorDevolucao.put(idDevolucao, isenta);
            return isenta.paraComprovante();
        }

        SolicitacaoDeCobranca solicitacao = new SolicitacaoDeCobranca(
                idMulta, cpfEstudante, valor, hoje.plusDays(DIAS_VENCIMENTO_BOLETO));
        CobrancaEmitida boleto = cobranca.emitirBoleto(solicitacao);

        Multa multa = new Multa(
                idMulta, idDevolucao, boleto.idCobranca(), cpfEstudante,
                diasDeAtraso, valor, politica.nome(), boleto.codigoBarras(), hoje);
        multasPorDevolucao.put(idDevolucao, multa);
        multasPorCobranca.put(boleto.idCobranca(), multa);
        return multa.paraComprovante();
    }

    @Override
    public ComprovanteDeMulta emitirSegundaVia(String idDevolucao) {
        Multa multa = multasPorDevolucao.get(idDevolucao);
        if (multa == null) {
            throw new RegistroNaoEncontradoException(
                    "Não existe multa emitida para a devolução " + idDevolucao + ".");
        }
        return multa.paraComprovante();
    }

    @Override
    public synchronized boolean registrarQuitacao(NotificacaoDePagamento notificacao) {
        Objects.requireNonNull(notificacao, "Notificação de pagamento é obrigatória.");
        Multa multa = multasPorCobranca.get(notificacao.idCobranca());
        if (multa == null) {
            throw new RegistroNaoEncontradoException(
                    "Cobrança não localizada no Financeiro: " + notificacao.idCobranca());
        }
        boolean quitadaAgora = multa.registrarQuitacao(notificacao.dataPagamento());
        if (quitadaAgora) {
            totalQuitacoesRegistradas++;
        }
        return quitadaAgora;
    }

    @Override
    public Optional<ComprovanteDeMulta> consultarPorDevolucao(String idDevolucao) {
        Multa multa = multasPorDevolucao.get(idDevolucao);
        return multa == null ? Optional.empty() : Optional.of(multa.paraComprovante());
    }

    @Override
    public List<ComprovanteDeMulta> listarMultas() {
        return multasPorDevolucao.values().stream().map(Multa::paraComprovante).toList();
    }

    public int getTotalQuitacoesRegistradas() {
        return totalQuitacoesRegistradas;
    }
}
