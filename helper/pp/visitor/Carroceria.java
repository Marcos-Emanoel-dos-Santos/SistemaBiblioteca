package visitor;

class Carroceria implements CarroElemento {

    public void accept(CarroElementoVisitante visitor) {
        visitor.visit(this);
    }
}