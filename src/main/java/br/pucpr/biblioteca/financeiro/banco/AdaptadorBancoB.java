package br.pucpr.biblioteca.financeiro.banco;

import br.pucpr.biblioteca.interfaces.iCobranca;
import br.pucpr.biblioteca.interfaces.iQuitacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * <b>R03 — Adapter: papel Adapter para o {@link BancoB}.</b>
 * <p>
 * Converte o contrato institucional {@link iCobranca} para {@link BancoB#abrirCobranca(String, BigDecimal)}
 * com documento formatado e valor decimal ({@code 7.00}), traduz situações e converte avisos
 * de pagamento do Banco B em {@link NotificacaoDePagamento}.
 */
public final class AdaptadorBancoB implements iCobranca {

    private final BancoB bancoB;

    public AdaptadorBancoB(BancoB bancoB) {
        this.bancoB = Objects.requireNonNull(bancoB, "Instância do Banco B é obrigatória.");
    }

    @Override
    public CobrancaEmitida emitirBoleto(SolicitacaoDeCobranca solicitacao) {
        Objects.requireNonNull(solicitacao, "Solicitação de cobrança é obrigatória.");
        String documento = formatarCpf(solicitacao.cpf());
        BigDecimal valorDecimal = solicitacao.valor().setScale(2, RoundingMode.HALF_UP);

        BancoB.CobrancaBancoB emitida = bancoB.abrirCobranca(documento, valorDecimal);
        return new CobrancaEmitida(
                emitida.codigoExterno(),
                "BANCO_B",
                emitida.codigoBoleto(),
                emitida.valorDecimal(),
                traduzirSituacao(emitida.situacaoExterna()));
    }

    @Override
    public SituacaoCobranca consultarSituacao(String idCobranca) {
        return traduzirSituacao(bancoB.obterSituacao(idCobranca));
    }

    public NotificacaoDePagamento traduzirNotificacao(BancoB.AvisoBancoB aviso) {
        Objects.requireNonNull(aviso, "Aviso do Banco B é obrigatório.");
        return new NotificacaoDePagamento(
                aviso.codigoExterno(),
                aviso.valorQuitado().setScale(2, RoundingMode.HALF_UP),
                aviso.dataConfirmacao());
    }

    public boolean encaminharNotificacao(BancoB.AvisoBancoB aviso, iQuitacao receptorQuitacao) {
        return receptorQuitacao.registrarQuitacao(traduzirNotificacao(aviso));
    }

    private static String formatarCpf(String cpf) {
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() == 11) {
            return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "."
                    + digitos.substring(6, 9) + "-" + digitos.substring(9, 11);
        }
        return cpf.trim();
    }

    private static SituacaoCobranca traduzirSituacao(String situacaoExterna) {
        if ("QUITADO".equals(situacaoExterna)) {
            return SituacaoCobranca.PAGA;
        }
        if ("PENDENTE_PAGAMENTO".equals(situacaoExterna)) {
            return SituacaoCobranca.PENDENTE;
        }
        return SituacaoCobranca.CANCELADA;
    }
}
