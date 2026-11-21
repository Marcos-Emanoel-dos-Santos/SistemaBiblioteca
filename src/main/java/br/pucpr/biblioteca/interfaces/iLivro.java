package br.pucpr.biblioteca.interfaces;

import br.pucpr.biblioteca.dto.DadosLivro;
import br.pucpr.biblioteca.dto.ResultadoDoCadastro;

import java.util.List;

/**
 * Contrato de manutenção de livros (Trabalho 02: interface {@code iLivro} do componente Biblioteca).
 * Implementada por {@code TratadorDeAcervo}.
 */
public interface iLivro {

    ResultadoDoCadastro<DadosLivro> inserirLivro(int idLivro, String titulo);

    DadosLivro consultarLivro(int idLivro);

    DadosLivro atualizarLivro(int idLivro, String novoTitulo);

    void excluirLivro(int idLivro);

    List<DadosLivro> listarLivros();
}
