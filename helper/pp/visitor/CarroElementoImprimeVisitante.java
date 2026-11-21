package visitor;

class CarroElementoImprimeVisitante implements CarroElementoVisitante {
    public void visit(Roda roda) {      
        System.out.println("Roda " + roda.pegaNome()
                            + " : visitando");
    }
 
    public void visit(Motor motor) {
        System.out.println("Motor : visitando");
    }
 
    public void visit(Carroceria carroceria) {
        System.out.println("Carroceria : visitando");
    }
 
    public void visit(Carro carro) {      
        System.out.println("Carro : visitando");
    }
}