package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.financeiro.banco.CobrancaEmitida;
import br.pucpr.biblioteca.financeiro.banco.SituacaoCobranca;
import br.pucpr.biblioteca.financeiro.banco.SolicitacaoDeCobranca;

/**
 * <b>R03 — Adapter: papel Target (contrato institucional estável de cobrança).</b>
 * <p>
 * O componente Financeiro depende exclusivamente desta interface para emitir boletos e consultar
 * sua situação, sem conhecer os formatos incompatíveis de Banco A ou Banco B.
 */
public interface iCobranca {

    CobrancaEmitida emitirBoleto(SolicitacaoDeCobranca solicitacao);

    SituacaoCobranca consultarSituacao(String idCobranca);
}
