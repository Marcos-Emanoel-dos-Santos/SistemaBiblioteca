package visitor;

public class VisitanteDemo {

    static public void main(String[] args) {
        Carro car = new Carro();
        car.accept(new CarroElementoImprimeVisitante());
        car.accept(new CarroElementoDoVisitante());
    }
}
