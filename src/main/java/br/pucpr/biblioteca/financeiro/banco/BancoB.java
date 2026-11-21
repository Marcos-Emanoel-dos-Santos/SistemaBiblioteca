package br.pucpr.biblioteca.financeiro.banco;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>R03 — Adapter: papel Adaptee (API externa incompatível do Banco B).</b>
 * <p>
 * Recebe documento formatado e <b>valor decimal</b> via {@link #abrirCobranca(String, BigDecimal)}
 * (ex.: R$ 7,00 = {@code 7.00}), usa códigos próprios ({@code "PENDENTE_PAGAMENTO"} /
 * {@code "QUITADO"}) e gera avisos de pagamento no seu formato próprio.
 */
public final class BancoB {

    public record CobrancaBancoB(String codigoExterno, String documento, BigDecimal valorDecimal,
                                 String codigoBoleto, String situacaoExterna) {
    }

    public record AvisoBancoB(String codigoExterno, BigDecimal valorQuitado,
                              LocalDate dataConfirmacao, String situacaoExterna) {
    }

    private final AtomicInteger sequencia = new AtomicInteger(5000);
    private final Map<String, CobrancaBancoB> cobrancas = new LinkedHashMap<>();

    public CobrancaBancoB abrirCobranca(String documento, BigDecimal valorDecimal) {
        String codigo = "BB-" + sequencia.getAndIncrement();
        BigDecimal normalizado = valorDecimal.setScale(2, RoundingMode.HALF_UP);
        String boleto = "23790-" + codigo + "-" + normalizado;
        CobrancaBancoB cobranca = new CobrancaBancoB(
                codigo, documento, normalizado, boleto, "PENDENTE_PAGAMENTO");
        cobrancas.put(codigo, cobranca);
        return cobranca;
    }

    public String obterSituacao(String codigoExterno) {
        CobrancaBancoB cobranca = cobrancas.get(codigoExterno);
        return cobranca == null ? "DESCONHECIDA" : cobranca.situacaoExterna();
    }

    /** Simula o pagamento pelo estudante diretamente no Banco B externo. */
    public AvisoBancoB simularPagamento(String codigoExterno, LocalDate dataConfirmacao) {
        CobrancaBancoB atual = cobrancas.get(codigoExterno);
        if (atual == null) {
            throw new IllegalArgumentException("Cobrança inexistente no Banco B: " + codigoExterno);
        }
        CobrancaBancoB quitada = new CobrancaBancoB(
                atual.codigoExterno(), atual.documento(), atual.valorDecimal(),
                atual.codigoBoleto(), "QUITADO");
        cobrancas.put(codigoExterno, quitada);
        return new AvisoBancoB(codigoExterno, atual.valorDecimal(), dataConfirmacao, "QUITADO");
    }

    public CobrancaBancoB buscarCobranca(String codigoExterno) {
        return cobrancas.get(codigoExterno);
    }
}
