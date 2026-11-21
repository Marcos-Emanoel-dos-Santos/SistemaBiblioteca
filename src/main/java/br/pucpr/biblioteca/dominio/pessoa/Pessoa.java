package br.pucpr.biblioteca.dominio.pessoa;

/**
 * Pessoa cadastrada no RH (UML: {@code Pessoa(nome, sobrenome, CPF)}).
 * Uma pessoa pode assumir até dois papéis: {@link Estudante} e {@link Atendente}.
 */
public class Pessoa {

    private final String cpf;
    private String nome;
    private String sobrenome;

    public Pessoa(String nome, String sobrenome, String cpf) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
    }

    /** Regra 2.2: atualiza os dados da pessoa (o CPF é a identidade e não muda). */
    public void atualizarPessoa(String nome, String sobrenome) {
        this.nome = nome;
        this.sobrenome = sobrenome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }
}
