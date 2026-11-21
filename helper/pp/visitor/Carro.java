package visitor;

class Carro implements CarroElemento {
    CarroElemento[] elementos;
 
    public CarroElemento[] pegaElementos() {
        return elementos.clone(); 
    }
 
    public Carro() {
        this.elementos = new CarroElemento[]
          { new Roda("Esquerda Dianteira"), new Roda("Direita Dianteria"),
            new Roda("Esquerda Traseira") , new Roda("Diretita Traseira"),
            new Carroceria(), new Motor() };
    }
 
    public void accept(CarroElementoVisitante visitante) {   
        for(CarroElemento elemento : this.pegaElementos()) {
            elemento.accept(visitante);
        }
        visitante.visit(this);       
    }
}
