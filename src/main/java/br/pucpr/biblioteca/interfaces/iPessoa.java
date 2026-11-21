package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.dto.DadosPessoa;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;

import java.util.List;

/**
 * Contrato de manutenção de pessoas (Trabalho 02: interface {@code iPessoa} do componente RH).
 * Implementada por {@code TratadorDePessoa}.
 */
public interface iPessoa {

    ResultadoDoCadastro<DadosPessoa> inserirPessoa(String nome, String sobrenome, String cpf);

    DadosPessoa consultarPessoa(String cpf);

    DadosPessoa atualizarPessoa(String cpf, String novoNome, String novoSobrenome);

    void excluirPessoa(String cpf);

    List<DadosPessoa> listarPessoas();
}
