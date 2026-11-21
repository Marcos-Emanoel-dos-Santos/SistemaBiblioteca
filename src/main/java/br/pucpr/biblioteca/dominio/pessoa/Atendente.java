package br.pucpr.biblioteca.dominio.pessoa;

/**
 * Papel de atendente, registrado no RH (UML: {@code Atendente(CPF, id_atendente)}).
 * O atributo {@code id_atendente} do UML é escrito {@code idAtendente} no código (convenção Java).
 */
public class Atendente extends Papel {

    private final int idAtendente;

    public Atendente(String cpf, int idAtendente) {
        super(cpf);
        this.idAtendente = idAtendente;
    }

    @Override
    public int getId() {
        return idAtendente;
    }

    public int getIdAtendente() {
        return idAtendente;
    }

    /** Regra 4.2: atualiza os dados do atendente (a pessoa associada). */
    public void atualizarAtendente(String cpf) {
        associarPessoa(cpf);
    }
}
