package Puzzle24;

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

    public Nodo(String estado, int nivel, Nodo padre) {
        this.estado = estado;
        this.nivel = nivel;
        this.padre = padre;
        this.costo = 0;
    }

    @Override
    public int compareTo(Nodo otro) {
        return Integer.compare(this.costo, otro.costo);
    }

    // ============ Generar sucesores ============
    public LinkedList<Nodo> generarSucesores() {
        LinkedList<Nodo> sucesores = new LinkedList<>();
        int indice = this.estado.indexOf(' ');
        int fila = indice / 5;
        int columna = indice % 5;

        int[][] movimientos = {{-1,0},{1,0},{0,-1},{0,1}};

        for (int[] mov : movimientos) {
            int nuevaFila = fila + mov[0];
            int nuevaCol = columna + mov[1];

            if (nuevaFila >= 0 && nuevaFila < 5 && nuevaCol >= 0 && nuevaCol < 5) {

                int nuevoIndice = nuevaFila * 5 + nuevaCol;
                String nuevoEstado = intercambiar(indice, nuevoIndice);

                if (padre == null || !nuevoEstado.equals(padre.estado))
                    sucesores.add(new Nodo(nuevoEstado, this.nivel + 1, this));
            }
        }
        return sucesores;
    }

    // ============ HEURISTICAS ============

    public int heuristicaManhattan(String objetivo) {
        int distancia = 0;

        for (int i = 0; i < estado.length(); i++) {
            char pieza = estado.charAt(i);

            if (pieza != ' ') {
                int indiceObjetivo = objetivo.indexOf(pieza);

                int filaActual = i / 5;
                int colActual = i % 5;

                int filaObjetivo = indiceObjetivo / 5;
                int colObjetivo = indiceObjetivo % 5;

                distancia += Math.abs(filaActual - filaObjetivo)
                        + Math.abs(colActual - colObjetivo);
            }
        }
        return distancia;
    }

    public int heuristicaConflictoLineal(String objetivo) {

        int manhattan = heuristicaManhattan(objetivo);
        int conflictos = 0;

        for (int fila = 0; fila < 5; fila++) {
            for (int col1 = 0; col1 < 5; col1++) {
                for (int col2 = col1 + 1; col2 < 5; col2++) {

                    int i = fila * 5 + col1;
                    int j = fila * 5 + col2;

                    char pieza1 = estado.charAt(i);
                    char pieza2 = estado.charAt(j);

                    if (pieza1 == ' ' || pieza2 == ' ') continue;

                    int posObj1 = objetivo.indexOf(pieza1);
                    int posObj2 = objetivo.indexOf(pieza2);

                    int filaObj1 = posObj1 / 5;
                    int filaObj2 = posObj2 / 5;

                    if (filaObj1 == fila && filaObj2 == fila && posObj1 > posObj2) {
                        conflictos++;
                    }
                }
            }
        }

        for (int col = 0; col < 5; col++) {
            for (int fila1 = 0; fila1 < 5; fila1++) {
                for (int fila2 = fila1 + 1; fila2 < 5; fila2++) {

                    int i = fila1 * 5 + col;
                    int j = fila2 * 5 + col;

                    char pieza1 = estado.charAt(i);
                    char pieza2 = estado.charAt(j);

                    if (pieza1 == ' ' || pieza2 == ' ') continue;

                    int posObj1 = objetivo.indexOf(pieza1);
                    int posObj2 = objetivo.indexOf(pieza2);

                    int colObj1 = posObj1 % 5;
                    int colObj2 = posObj2 % 5;

                    if (colObj1 == col && colObj2 == col && posObj1 > posObj2) {
                        conflictos++;
                    }
                }
            }
        }

        return manhattan + (2 * conflictos);
    }

    public int heuristicaEsquinas(String objetivo) {
        int conflictosEsquina = 0;
        int maxIndice = 24;

        // Definición de las 4 esquinas: {Índice, VecinoHorizontal, VecinoVertical}
        int[][] esquinas = {
                {0, 1, 5},
                {4, 3, 9},
                {20, 21, 15},
                {24, 23, 19}
        };

        for (int[] esq : esquinas) {
            int idxEsq = esq[0];
            int idxVecinoH = esq[1];
            int idxVecinoV = esq[2];

            char piezaEsq = estado.charAt(idxEsq);
            char metaEsq = objetivo.charAt(idxEsq);

            if (piezaEsq == metaEsq && piezaEsq != ' ') {

                char piezaVecinoH = estado.charAt(idxVecinoH);
                char piezaVecinoV = estado.charAt(idxVecinoV);
                char metaVecinoH = objetivo.charAt(idxVecinoH);
                char metaVecinoV = objetivo.charAt(idxVecinoV);

                if (piezaVecinoH != metaVecinoH && piezaVecinoV != metaVecinoV) {
                    conflictosEsquina++;
                }
            }
        }
        return heuristicaConflictoLineal(objetivo) + (2 * conflictosEsquina);
    }

    //============ OTROS METODOS ============
    private String intercambiar(int i, int j) {
        StringBuilder sb = new StringBuilder(this.estado);
        char temp = sb.charAt(i);
        sb.setCharAt(i, sb.charAt(j));
        sb.setCharAt(j, temp);
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
            for (int i = 0; i < n.estado.length(); i++) {
                System.out.print("[" + n.estado.charAt(i) + "] ");
                if ((i + 1) % 5 == 0) System.out.println();
            }
            System.out.println("Nivel: " + n.nivel);
            System.out.println("-------------------------------");
        }
    }
}