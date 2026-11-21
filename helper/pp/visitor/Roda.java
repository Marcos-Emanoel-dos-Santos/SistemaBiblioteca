package visitor;

class Roda implements CarroElemento {

    private String nome;

    public Roda(String nome) {
        this.nome = nome;
    }

    public String pegaNome() {
        return this.nome;
    }

    public void accept(CarroElementoVisitante visitor) {
        visitor.visit(this);
    }
}
