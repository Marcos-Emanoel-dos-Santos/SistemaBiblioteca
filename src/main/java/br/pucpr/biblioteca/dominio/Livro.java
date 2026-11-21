package br.pucpr.biblioteca.dominio;

import br.pucpr.biblioteca.dominio.estado.EstadoLivro;
import br.pucpr.biblioteca.dominio.estado.EstadosDoLivro;
import br.pucpr.biblioteca.dominio.transacao.Emprestimo;

/**
 * Livro do acervo (UML: {@code Livro(idLivro, titulo, estado)}).
 * <p>
 * <b>R01 — State: papel Context.</b> O atributo {@code estado} é um objeto {@link EstadoLivro}
 * (e não uma {@code String}); cada evento do ciclo de vida é delegado diretamente ao estado atual,
 * eliminando condicionais de estado nos tratadores e na fachada.
 */
public class Livro {

    private final int idLivro;
    private String titulo;
    private EstadoLivro estado;

    /** T1 — inserirLivro: todo livro novo nasce LIVRE. */
    public Livro(int idLivro, String titulo) {
        this(idLivro, titulo, EstadosDoLivro.LIVRE);
    }

    private Livro(int idLivro, String titulo, EstadoLivro estado) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.estado = estado;
    }

    /** Reconstitui um livro lido da persistência, com o estado que estava gravado. */
    public static Livro reconstituir(int idLivro, String titulo, String nomeDoEstado) {
        return new Livro(idLivro, titulo, EstadosDoLivro.porNome(nomeDoEstado));
    }

    /** Regra 1.2: atualiza os dados do livro (o id é a identidade e não muda). */
    public void atualizarLivro(String titulo) {
        this.titulo = titulo;
    }

    // --- Eventos delegados ao objeto de estado (R01 — State) ---

    public void reservarLivro() {
        this.estado = estado.reservarLivro();
    }

    public void removerReserva() {
        this.estado = estado.removerReserva();
    }

    public void emprestarLivro() {
        this.estado = estado.emprestarLivro();
    }

    public void renovarEmprestimo(Emprestimo emprestimo, int diasRenovacao) {
        this.estado = estado.renovarEmprestimo(emprestimo, diasRenovacao);
    }

    public void registrarDevolucao() {
        this.estado = estado.registrarDevolucao();
    }

    public void inspecionarLivro(boolean aprovado) {
        this.estado = estado.inspecionarLivro(aprovado);
    }

    public void enviarParaReparo() {
        this.estado = estado.enviarParaReparo();
    }

    public void retornarDoReparo() {
        this.estado = estado.retornarDoReparo();
    }

    public void registrarLaudoIrrecuperavel() {
        this.estado = estado.registrarLaudoIrrecuperavel();
    }

    public void avaliarReparo(boolean consertado) {
        this.estado = estado.avaliarReparo(consertado);
    }

    public void validarExclusao() {
        estado.validarExclusao();
    }

    public int getIdLivro() {
        return idLivro;
    }

    public String getTitulo() {
        return titulo;
    }

    public EstadoLivro getEstado() {
        return estado;
    }
}
