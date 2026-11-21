package br.pucpr.biblioteca.financeiro;

import br.pucpr.biblioteca.dto.ComprovanteDeMulta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Multa emitida pelo componente Financeiro.
 * <p>
 * O {@code valor} e a {@code politicaAplicada} são gravados como campos {@code final} no momento
 * da emissão; portanto, trocar a política vigente nunca recalcula multas já emitidas (R02).
 */
public final class Multa {

    public enum Situacao {
        EMITIDA,
        QUITADA
    }

    private final String idMulta;
    private final String idDevolucao;
    private final String idCobranca;
    private final String cpfEstudante;
    private final long diasDeAtraso;
    private final BigDecimal valor;
    private final String politicaAplicada;
    private final String codigoBarras;
    private final LocalDate dataEmissao;

    private Situacao situacao;
    private LocalDate dataQuitacao;

    public Multa(String idMulta, String idDevolucao, String idCobranca, String cpfEstudante,
                 long diasDeAtraso, BigDecimal valor, String politicaAplicada,
                 String codigoBarras, LocalDate dataEmissao) {
        this.idMulta = Objects.requireNonNull(idMulta, "idMulta é obrigatório.");
        this.idDevolucao = Objects.requireNonNull(idDevolucao, "idDevolucao é obrigatório.");
        this.idCobranca = Objects.requireNonNull(idCobranca, "idCobranca é obrigatório.");
        this.cpfEstudante = Objects.requireNonNull(cpfEstudante, "cpfEstudante é obrigatório.");
        this.diasDeAtraso = diasDeAtraso;
        this.valor = Objects.requireNonNull(valor, "valor é obrigatório.");
        this.politicaAplicada = Objects.requireNonNull(politicaAplicada, "politicaAplicada é obrigatória.");
        this.codigoBarras = Objects.requireNonNull(codigoBarras, "codigoBarras é obrigatório.");
        this.dataEmissao = Objects.requireNonNull(dataEmissao, "dataEmissao é obrigatória.");
        this.situacao = Situacao.EMITIDA;
    }

    /**
     * Marca a multa como quitada de forma idempotente.
     *
     * @return {@code true} se a quitação foi registrada agora; {@code false} se já estava quitada.
     */
    public boolean registrarQuitacao(LocalDate dataPagamento) {
        if (this.situacao == Situacao.QUITADA) {
            return false;
        }
        this.situacao = Situacao.QUITADA;
        this.dataQuitacao = Objects.requireNonNull(dataPagamento, "Data de pagamento é obrigatória.");
        return true;
    }

    public String getIdMulta() {
        return idMulta;
    }

    public String getIdDevolucao() {
        return idDevolucao;
    }

    public String getIdCobranca() {
        return idCobranca;
    }

    public String getCpfEstudante() {
        return cpfEstudante;
    }

    public long getDiasDeAtraso() {
        return diasDeAtraso;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getPoliticaAplicada() {
        return politicaAplicada;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public LocalDate getDataQuitacao() {
        return dataQuitacao;
    }

    public ComprovanteDeMulta paraComprovante() {
        return new ComprovanteDeMulta(
                idMulta, idDevolucao, idCobranca, cpfEstudante, diasDeAtraso,
                valor, politicaAplicada, situacao.name(), codigoBarras, dataEmissao, dataQuitacao);
    }
}
