package com.ees.pp.MarkerInterface;

public abstract class Documento {
    private String texto;

    public Documento(String texto) {
        this.texto = texto;
    }

    public void compacta() { 
    // simplesmente, acrescenta .zip o texto
        if (this instanceof Comprimivel) {
            System.out.println(texto + ".ZIP");
        }
    }
    public void imprima() {
        if (this instanceof Imprimivel) {
            System.out.println(texto);
        }
    }
    public void criptografe() 
    // simplesmente, inverte os caracteres
    {
        if (this instanceof Confidencial) {
            StringBuffer sb = new StringBuffer(texto);
            StringBuffer invertida = sb.reverse();
            texto = invertida.toString();
        }
    }
}
