package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.dto.DadosAtendente;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;

import java.util.List;

/**
 * Contrato de manutenção de atendentes (Trabalho 02: interface {@code iAtendente} do componente RH).
 * Implementada por {@code TratadorDeAtendente}.
 */
public interface iAtendente {

    ResultadoDoCadastro<DadosAtendente> inserirAtendente(String cpf, int idAtendente);

    DadosAtendente consultarAtendente(int idAtendente);

    DadosAtendente atualizarAtendente(int idAtendente, String novoCpf);

    void excluirAtendente(int idAtendente);

    List<DadosAtendente> listarAtendentes();
}
