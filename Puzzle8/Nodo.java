package Puzzle8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Nodo implements Comparable<Nodo> {

    String estado;
    int nivel;
    int costo;
    Nodo padre;

    public Nodo(String estado) {
        this.estado = estado;
        this.nivel = 0;
        this.costo = 0;
        this.padre = null;
    }

    public Nodo(String estado, int nivel) {
        this.estado = estado;
        this.nivel = nivel;
        this.costo = 0;
        this.padre = null;
    }

    public Nodo(String estado, int nivel, Nodo padre) {
        this.estado = estado;
        this.nivel = nivel;
        this.costo = 0;
        this.padre = padre;
    }

    public Nodo(String estado, int nivel, int costo, Nodo padre) {
        this.estado = estado;
        this.nivel = nivel;
        this.costo = costo;
        this.padre = padre;
    }

    @Override
    public int compareTo(Nodo otro) {
        return Integer.compare(this.costo, otro.costo);
    }


    public LinkedList<Nodo> generarSucesores() {
        LinkedList<Nodo> sucesores = new LinkedList<>();

        int indice = this.estado.indexOf(' ');
        int nuevoNivel = this.nivel + 1;
        int nuevoCosto = this.costo + 1;

        switch (indice) {
            case 0:
                sucesores.add(new Nodo(intercambiar(this.estado, 0, 1), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 0, 3), nuevoNivel, nuevoCosto, this));
                break;

            case 1:
                sucesores.add(new Nodo(intercambiar(this.estado, 1, 0), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 1, 2), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 1, 4), nuevoNivel, nuevoCosto, this));
                break;

            case 2:
                sucesores.add(new Nodo(intercambiar(this.estado, 2, 1), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 2, 5), nuevoNivel, nuevoCosto, this));
                break;

            case 3:
                sucesores.add(new Nodo(intercambiar(this.estado, 3, 0), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 3, 4), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 3, 6), nuevoNivel, nuevoCosto, this));
                break;

            case 4:
                sucesores.add(new Nodo(intercambiar(this.estado, 4, 1), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 4, 3), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 4, 5), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 4, 7), nuevoNivel, nuevoCosto, this));
                break;

            case 5:
                sucesores.add(new Nodo(intercambiar(this.estado, 5, 2), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 5, 4), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 5, 8), nuevoNivel, nuevoCosto, this));
                break;

            case 6:
                sucesores.add(new Nodo(intercambiar(this.estado, 6, 3), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 6, 7), nuevoNivel, nuevoCosto, this));
                break;

            case 7:
                sucesores.add(new Nodo(intercambiar(this.estado, 7, 4), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 7, 6), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 7, 8), nuevoNivel, nuevoCosto, this));
                break;

            case 8:
                sucesores.add(new Nodo(intercambiar(this.estado, 8, 5), nuevoNivel, nuevoCosto, this));
                sucesores.add(new Nodo(intercambiar(this.estado, 8, 7), nuevoNivel, nuevoCosto, this));
                break;

            default:
                break;
        }

        return sucesores;
    }

    private String intercambiar(String estado, int i, int j) {
        StringBuilder sb = new StringBuilder(estado);
        char a = estado.charAt(i);
        char b = estado.charAt(j);
        sb.setCharAt(i, b);
        sb.setCharAt(j, a);
        return sb.toString();
    }

    public void imprimirCamino() {
        ArrayList<Nodo> camino = new ArrayList<>();
        Nodo actual = this;

        while (actual != null) {
            camino.add(actual);
            actual = actual.padre;
        }

        Collections.reverse(camino);

        for (Nodo n : camino) {
            for (int i = 0; i < 9; i++) {
                System.out.print("[" + n.estado.charAt(i) + "] ");
                if ((i + 1) % 3 == 0) {
                    System.out.println();
                }
            }
            System.out.println("Nivel: " + n.nivel + " | Costo g(n): " + n.costo);
            System.out.println("----------------");
        }
    }
}