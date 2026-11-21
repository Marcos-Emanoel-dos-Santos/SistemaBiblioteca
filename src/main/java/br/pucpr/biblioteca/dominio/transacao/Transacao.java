package br.pucpr.biblioteca.dominio.transacao;

import java.time.LocalDate;

/**
 * Transação de circulação (UML: {@code Transacao(data, dataDeVencimento)}).
 * "Toda transação ocorre em uma data e envolve um livro e um estudante."
 * <p>
 * As datas do UML ({@code String}) são representadas por {@link LocalDate} para permitir
 * o cálculo de dias corridos (renovação +30 dias, dias de atraso).
 */
public abstract class Transacao {

    private final String id;
    private final int idLivro;
    private final int matricula;
    private final LocalDate data;
    private LocalDate dataDeVencimento;

    protected Transacao(String id, int idLivro, int matricula, LocalDate data, LocalDate dataDeVencimento) {
        this.id = id;
        this.idLivro = idLivro;
        this.matricula = matricula;
        this.data = data;
        this.dataDeVencimento = dataDeVencimento;
    }

    public String getId() {
        return id;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public int getMatricula() {
        return matricula;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalDate getDataDeVencimento() {
        return dataDeVencimento;
    }

    protected void alterarDataDeVencimento(LocalDate novaData) {
        this.dataDeVencimento = novaData;
    }
}
