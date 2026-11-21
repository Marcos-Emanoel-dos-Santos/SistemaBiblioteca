package visitor;

class Motor implements CarroElemento {

    public void accept(CarroElementoVisitante visitor) {
        visitor.visit(this);
    }
}
