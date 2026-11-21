package visitor;

public interface CarroElementoVisitante {

    void visit(Roda wheel);

    void visit(Motor engine);

    void visit(Carroceria body);

    void visit(Carro car);
}
