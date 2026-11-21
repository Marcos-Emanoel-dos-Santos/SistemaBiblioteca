package visitor;

class CarroElementoDoVisitante implements CarroElementoVisitante {
    public void visit(Roda roda) {
        System.out.println("Minha Roda " + roda.pegaNome() + " : girando");
    }
 
    public void visit(Motor motor) {
        System.out.println("Meu motor : ligando");
    }
 
    public void visit(Carroceria carroceria) {
        System.out.println("Minha Carroceria: movendo");
    }
 
    public void visit(Carro carro) {
        System.out.println("Meu Carro: lignado");
    }
}
