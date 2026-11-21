package br.pucpr.biblioteca.circulacao;

import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dominio.excecao.RegistroNaoEncontradoException;
import br.pucpr.biblioteca.dominio.pessoa.Estudante;
import br.pucpr.biblioteca.dominio.transacao.Devolucao;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;
import br.pucpr.biblioteca.dominio.transacao.Reserva;
import br.pucpr.biblioteca.dto.ComprovanteDeDevolucao;
import br.pucpr.biblioteca.dto.ComprovanteDeEmprestimo;
import br.pucpr.biblioteca.dto.ComprovanteDeRenovacao;
import br.pucpr.biblioteca.dto.ComprovanteDeReserva;
import br.pucpr.biblioteca.interfaces.iCirculacao;
import br.pucpr.biblioteca.nucleo.AcervoDeLivros;
import br.pucpr.biblioteca.nucleo.Movimentacao;
import br.pucpr.biblioteca.nucleo.Relacionamento;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>R04 — Facade: papel Facade.</b>
 * <p>
 * Coordena {@link AcervoDeLivros}, {@link Relacionamento}, {@link Movimentacao} e {@link Livro}
 * dentro de transações da {@link UnidadeDeTrabalho}, expondo operações coesas de circulação que
 * devolvem comprovantes imutáveis (sem expor coleções internas à interface).
 * <p>
 * Não contém condicionais sobre o estado do livro: todas as transições são delegadas ao próprio
 * {@link Livro} (R01 — State).
 */
public final class FachadaDeCirculacao implements iCirculacao {

    private final UnidadeDeTrabalho unidadeDeTrabalho;
    private final AcervoDeLivros acervo;
    private final Relacionamento relacionamento;
    private final Movimentacao movimentacao;
    private final Clock relogio;
    private final int diasValidadeReserva;
    private final int diasPrazoEmprestimo;
    private final int diasRenovacao;
    private final AtomicInteger sequencia = new AtomicInteger(1);

    public FachadaDeCirculacao(UnidadeDeTrabalho unidadeDeTrabalho,
                               AcervoDeLivros acervo,
                               Relacionamento relacionamento,
                               Movimentacao movimentacao,
                               Clock relogio,
                               int diasValidadeReserva,
                               int diasPrazoEmprestimo,
                               int diasRenovacao) {
        this.unidadeDeTrabalho = Objects.requireNonNull(unidadeDeTrabalho, "Unidade de trabalho é obrigatória.");
        this.acervo = Objects.requireNonNull(acervo, "Acervo é obrigatório.");
        this.relacionamento = Objects.requireNonNull(relacionamento, "Relacionamento é obrigatório.");
        this.movimentacao = Objects.requireNonNull(movimentacao, "Movimentacao é obrigatória.");
        this.relogio = Objects.requireNonNull(relogio, "Relógio é obrigatório.");
        this.diasValidadeReserva = diasValidadeReserva;
        this.diasPrazoEmprestimo = diasPrazoEmprestimo;
        this.diasRenovacao = diasRenovacao;
    }

    @Override
    public ComprovanteDeReserva reservarLivro(int idLivro, int matricula) {
        return unidadeDeTrabalho.executar(() -> {
            // Regra 5.1 e 5.2: valida fonte autoritativa do livro e cadastro do estudante
            Livro livro = exigirLivro(idLivro);
            exigirEstudante(matricula);

            // Regra 5.3 + R01 State: o próprio estado atual decide se aceita reserva (LIVRE -> RESERVADO)
            livro.reservarLivro();

            LocalDate hoje = LocalDate.now(relogio);
            LocalDate vencimentoReserva = hoje.plusDays(diasValidadeReserva);
            Reserva reserva = new Reserva(
                    "RES-" + sequencia.getAndIncrement(), idLivro, matricula, hoje, vencimentoReserva);

            movimentacao.reservarLivro(reserva);
            acervo.atualizarLivro(livro);

            return new ComprovanteDeReserva(
                    reserva.getId(), livro.getIdLivro(), livro.getTitulo(),
                    matricula, reserva.getData(), reserva.getDataDeVencimento());
        });
    }

    @Override
    public ComprovanteDeReserva consultarReserva(int idLivro, int matricula) {
        Reserva reserva = movimentacao.consultarReserva(idLivro, matricula)
                .orElseThrow(() -> new RegistroNaoEncontradoException(
                        "Reserva não encontrada para livro " + idLivro + " e estudante " + matricula + "."));
        Livro livro = exigirLivro(idLivro);
        return new ComprovanteDeReserva(
                reserva.getId(), livro.getIdLivro(), livro.getTitulo(),
                matricula, reserva.getData(), reserva.getDataDeVencimento());
    }

    @Override
    public ComprovanteDeEmprestimo emprestarLivro(int idLivro, int matricula) {
        return unidadeDeTrabalho.executar(() -> {
            // Regra 7.1: valida reserva ativa registrada para o mesmo estudante
            Reserva reserva = movimentacao.consultarReserva(idLivro, matricula)
                    .orElseThrow(() -> new RegistroNaoEncontradoException(
                            "Não há reserva ativa do livro " + idLivro + " para o estudante " + matricula + "."));
            Livro livro = exigirLivro(idLivro);

            // Regra 7.2 + R01 State: RESERVADO -> EMPRESTADO
            livro.emprestarLivro();

            LocalDate hoje = LocalDate.now(relogio);
            LocalDate vencimento = hoje.plusDays(diasPrazoEmprestimo);
            Emprestimo emprestimo = new Emprestimo(
                    "EMP-" + sequencia.getAndIncrement(), idLivro, matricula, hoje, vencimento, 0);

            // Ordem alinhada ao UML: remover reserva, criar empréstimo e atualizar livro na mesma transação
            movimentacao.removerReserva(reserva.getId());
            movimentacao.criarEmprestimo(emprestimo);
            acervo.atualizarLivro(livro);

            return new ComprovanteDeEmprestimo(
                    emprestimo.getId(), livro.getIdLivro(), livro.getTitulo(),
                    matricula, emprestimo.getData(), emprestimo.getDataDeVencimento(),
                    livro.getEstado().nome());
        });
    }

    @Override
    public ComprovanteDeRenovacao renovarEmprestimo(int idLivro, int matricula) {
        return unidadeDeTrabalho.executar(() -> {
            // Regra 8.1: localiza empréstimo ativo do livro para o estudante
            Emprestimo emprestimo = movimentacao.consultarEmprestimo(idLivro, matricula)
                    .orElseThrow(() -> new RegistroNaoEncontradoException(
                            "Empréstimo ativo não encontrado para livro " + idLivro + " e estudante " + matricula + "."));
            Livro livro = exigirLivro(idLivro);

            // Regras 8.2/8.3 + R01 State: guarda numRenovacoes < 2 é avaliada no estado antes de somar +30 dias
            livro.renovarEmprestimo(emprestimo, diasRenovacao);

            movimentacao.renovar(emprestimo);
            acervo.atualizarLivro(livro);

            return new ComprovanteDeRenovacao(
                    emprestimo.getId(), livro.getIdLivro(), matricula,
                    emprestimo.getNumRenovacoes(), emprestimo.getDataDeVencimento(),
                    livro.getEstado().nome());
        });
    }

    @Override
    public ComprovanteDeDevolucao registrarDevolucao(int idLivro, int matricula) {
        return unidadeDeTrabalho.executar(() -> {
            // Regra 9.1: localiza empréstimo ativo
            Emprestimo emprestimo = movimentacao.consultarEmprestimo(idLivro, matricula)
                    .orElseThrow(() -> new RegistroNaoEncontradoException(
                            "Empréstimo ativo não encontrado para livro " + idLivro + " e estudante " + matricula + "."));
            Livro livro = exigirLivro(idLivro);
            Estudante estudante = exigirEstudante(matricula);

            // Regra 9.2 + R01 State: EMPRESTADO/RENOVADO -> DEVOLVIDO (aguarda inspeção)
            livro.registrarDevolucao();

            LocalDate dataDevolucao = LocalDate.now(relogio);
            Devolucao devolucao = new Devolucao(
                    "DEV-" + sequencia.getAndIncrement(), idLivro, matricula,
                    dataDevolucao, emprestimo.getDataDeVencimento());

            movimentacao.criarDevolucao(devolucao);
            movimentacao.removerEmprestimo(emprestimo.getId());
            acervo.atualizarLivro(livro);

            return new ComprovanteDeDevolucao(
                    devolucao.getId(), livro.getIdLivro(), livro.getTitulo(),
                    matricula, estudante.getCpf(), devolucao.getData(),
                    devolucao.getDataDeVencimento(), devolucao.getDiasDeAtraso(),
                    livro.getEstado().nome());
        });
    }

    private Livro exigirLivro(int idLivro) {
        return acervo.consultarLivro(idLivro)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Livro não cadastrado: " + idLivro + "."));
    }

    private Estudante exigirEstudante(int matricula) {
        return relacionamento.consultarEstudante(matricula)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Estudante não cadastrado: " + matricula + "."));
    }
}
