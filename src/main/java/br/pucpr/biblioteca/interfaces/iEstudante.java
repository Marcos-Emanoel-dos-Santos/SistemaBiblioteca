package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.dto.DadosEstudante;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;

import java.util.List;

/**
 * Contrato de manutenção de estudantes (Trabalho 02: interface {@code iEstudante} do componente Acadêmico).
 * Implementada por {@code TratadorDeEstudante}.
 */
public interface iEstudante {

    ResultadoDoCadastro<DadosEstudante> inserirEstudante(String cpf, int matricula);

    DadosEstudante consultarEstudante(int matricula);

    DadosEstudante atualizarEstudante(int matricula, String novoCpf);

    void excluirEstudante(int matricula);

    List<DadosEstudante> listarEstudantes();
}
