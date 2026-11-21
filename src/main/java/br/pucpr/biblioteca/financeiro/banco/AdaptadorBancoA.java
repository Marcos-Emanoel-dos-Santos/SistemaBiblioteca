package br.pucpr.biblioteca.financeiro.banco;

import br.pucpr.biblioteca.interfaces.iCobranca;
import br.pucpr.biblioteca.interfaces.iQuitacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * <b>R03 — Adapter: papel Adapter para o {@link BancoA}.</b>
 * <p>
 * Converte valores decimais (R$ 7,00) em centavos ({@code 700}), limpa a formatação do CPF,
 * traduz identificadores/situações do Banco A para o contrato institucional {@link iCobranca}
 * e converte notificações de pagamento do Banco A em {@link NotificacaoDePagamento}.
 */
public final class AdaptadorBancoA implements iCobranca {

    private static final BigDecimal CEM = new BigDecimal("100");

    private final BancoA bancoA;

    public AdaptadorBancoA(BancoA bancoA) {
        this.bancoA = Objects.requireNonNull(bancoA, "Instância do Banco A é obrigatória.");
    }

    @Override
    public CobrancaEmitida emitirBoleto(SolicitacaoDeCobranca solicitacao) {
        Objects.requireNonNull(solicitacao, "Solicitação de cobrança é obrigatória.");
        String cpfSomenteDigitos = solicitacao.cpf().replaceAll("\\D", "");
        long centavos = solicitacao.valor()
                .multiply(CEM)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        BancoA.TituloBancoA titulo = bancoA.gerarTitulo(cpfSomenteDigitos, centavos);
        return new CobrancaEmitida(
                titulo.numeroTitulo(),
                "BANCO_A",
                titulo.linhaDigitavel(),
                deCentavos(titulo.valorEmCentavos()),
                traduzirSituacao(titulo.statusBancoA()));
    }

    @Override
    public SituacaoCobranca consultarSituacao(String idCobranca) {
        return traduzirSituacao(bancoA.consultarStatusTitulo(idCobranca));
    }

    public NotificacaoDePagamento traduzirNotificacao(BancoA.NotificacaoBancoA notificacao) {
        Objects.requireNonNull(notificacao, "Notificação do Banco A é obrigatória.");
        return new NotificacaoDePagamento(
                notificacao.numeroTitulo(),
                deCentavos(notificacao.valorPagoEmCentavos()),
                LocalDate.parse(notificacao.dataLiquidacaoIso()));
    }

    public boolean encaminharNotificacao(BancoA.NotificacaoBancoA notificacao, iQuitacao receptorQuitacao) {
        return receptorQuitacao.registrarQuitacao(traduzirNotificacao(notificacao));
    }

    private static BigDecimal deCentavos(long centavos) {
        return BigDecimal.valueOf(centavos).divide(CEM, 2, RoundingMode.HALF_UP);
    }

    private static SituacaoCobranca traduzirSituacao(String statusBancoA) {
        if ("LIQUIDADO".equals(statusBancoA)) {
            return SituacaoCobranca.PAGA;
        }
        if ("ABERTO".equals(statusBancoA)) {
            return SituacaoCobranca.PENDENTE;
        }
        return SituacaoCobranca.CANCELADA;
    }
}
