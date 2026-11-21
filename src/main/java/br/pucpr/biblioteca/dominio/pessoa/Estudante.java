package br.pucpr.biblioteca.dominio.pessoa;

/** Papel de estudante, registrado no Acadêmico (UML: {@code Estudante(CPF, matricula)}). */
public class Estudante extends Papel {

    private final int matricula;

    public Estudante(String cpf, int matricula) {
        super(cpf);
        this.matricula = matricula;
    }

    @Override
    public int getId() {
        return matricula;
    }

    public int getMatricula() {
        return matricula;
    }

    /** Regra 3.2: atualiza os dados do estudante (a pessoa associada). */
    public void atualizarEstudante(String cpf) {
        associarPessoa(cpf);
    }
}
