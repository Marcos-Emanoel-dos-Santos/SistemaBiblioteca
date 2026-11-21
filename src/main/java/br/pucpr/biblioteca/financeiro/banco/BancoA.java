package br.pucpr.biblioteca.financeiro.banco;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>R03 — Adapter: papel Adaptee (API externa incompatível do Banco A).</b>
 * <p>
 * Recebe CPF somente em dígitos e valor inteiro em <b>centavos</b> via
 * {@link #gerarTitulo(String, long)} (ex.: R$ 7,00 = {@code 700}), usa códigos próprios de
 * situação ({@code "ABERTO"} / {@code "LIQUIDADO"}) e emite notificações nesse mesmo formato.
 */
public final class BancoA {

    public record TituloBancoA(String numeroTitulo, String cpfSomenteDigitos, long valorEmCentavos,
                               String linhaDigitavel, String statusBancoA) {
    }

    public record NotificacaoBancoA(String numeroTitulo, long valorPagoEmCentavos,
                                    String dataLiquidacaoIso, String statusBancoA) {
    }

    private final AtomicInteger sequencia = new AtomicInteger(1000);
    private final Map<String, TituloBancoA> titulos = new LinkedHashMap<>();

    public TituloBancoA gerarTitulo(String cpf, long centavos) {
        String numero = "BA-" + sequencia.getAndIncrement();
        String linha = "00190." + numero + "." + centavos;
        TituloBancoA titulo = new TituloBancoA(numero, cpf, centavos, linha, "ABERTO");
        titulos.put(numero, titulo);
        return titulo;
    }

    public String consultarStatusTitulo(String numeroTitulo) {
        TituloBancoA titulo = titulos.get(numeroTitulo);
        return titulo == null ? "INEXISTENTE" : titulo.statusBancoA();
    }

    /** Simula o pagamento pelo estudante diretamente no Banco A externo. */
    public NotificacaoBancoA simularPagamento(String numeroTitulo, LocalDate dataPagamento) {
        TituloBancoA atual = titulos.get(numeroTitulo);
        if (atual == null) {
            throw new IllegalArgumentException("Título inexistente no Banco A: " + numeroTitulo);
        }
        TituloBancoA liquidado = new TituloBancoA(
                atual.numeroTitulo(), atual.cpfSomenteDigitos(), atual.valorEmCentavos(),
                atual.linhaDigitavel(), "LIQUIDADO");
        titulos.put(numeroTitulo, liquidado);
        return new NotificacaoBancoA(
                numeroTitulo, atual.valorEmCentavos(), dataPagamento.toString(), "LIQUIDADO");
    }

    public TituloBancoA buscarTitulo(String numeroTitulo) {
        return titulos.get(numeroTitulo);
    }
}
