package br.pucpr.biblioteca.dominio.pessoa;

/**
 * Papel assumido por uma {@link Pessoa} (UML: Pessoa 1 — assume — 0..2 Papel).
 * <p>
 * "Todo papel tem um id": a matrícula no {@link Estudante} e o id do {@link Atendente}.
 * O papel referencia a pessoa pelo CPF, que é a identidade da pessoa no RH.
 */
public abstract class Papel {

    private String cpf;

    protected Papel(String cpf) {
        this.cpf = cpf;
    }

    /** Identificador do papel. */
    public abstract int getId();

    public String getCpf() {
        return cpf;
    }

    /** Reassocia o papel a outra pessoa já cadastrada (regras 3.2 e 4.2). */
    protected void associarPessoa(String novoCpf) {
        this.cpf = novoCpf;
    }
}
