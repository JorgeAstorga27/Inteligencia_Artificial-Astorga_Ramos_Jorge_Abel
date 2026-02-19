package Diagnostico;

public class App {
    public static void main(String[] args) {
        Arbol miArbol = new Arbol();

        Nodo n1 = new Nodo("Marta");
        Nodo n2 = new Nodo("Ana");
        Nodo n3 = new Nodo("Zulema");

        miArbol.raiz = n1;
        n1.izquierdo = n2;
        n1.derecho = n3;

        Nodo r1 = miArbol.buscarNodo("Ana");
        System.out.println("Busqueda 'Ana': " + (r1 != null ? r1.nombre : "null"));

        Nodo r2 = miArbol.buscarNodo("Zulema");
        System.out.println("Busqueda 'Zulema': " + (r2 != null ? r2.nombre : "null"));

        Nodo r3 = miArbol.buscarNodo("Pedro");
        System.out.println("Busqueda 'Pedro': " + (r3 != null ? r3.nombre : "null"));
    }
}