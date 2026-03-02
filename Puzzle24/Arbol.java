package Puzzle24;

import java.util.*;

public class Arbol {

    Nodo raiz;
    public int nodosExplorados;
    private Nodo solucion;

    public Arbol(Nodo raiz) {
        this.raiz = raiz;
    }

    // ============ BUSQUEDAS ============

    Nodo IDAStar(String objetivo, int tipoHeuristica) {
        nodosExplorados = 0;
        solucion = null;

        // Inicializar límite con la heurística seleccionada
        int limite = obtenerH(raiz, objetivo, tipoHeuristica);

        while (true) {
            int resultado = buscarIDA(raiz, 0, limite, objetivo, tipoHeuristica);

            if (resultado == -1) return solucion;
            if (resultado == Integer.MAX_VALUE) return null;

            limite = resultado;
        }
    }

    private int buscarIDA(Nodo nodo, int g, int limite, String objetivo, int tipoHeuristica) {
        int h = obtenerH(nodo, objetivo, tipoHeuristica);
        int f = g + h;

        if (f > limite) return f;

        nodosExplorados++;

        if (nodo.estado.equals(objetivo)) {
            solucion = nodo;
            return -1;
        }

        int minimo = Integer.MAX_VALUE;

        for (Nodo hijo : nodo.generarSucesores()) {
            int temp = buscarIDA(hijo, g + 1, limite, objetivo, tipoHeuristica);

            if (temp == -1) return -1;
            if (temp < minimo) minimo = temp;
        }
        return minimo;
    }

    private int obtenerH(Nodo n, String obj, int tipo) {
        return switch (tipo) {
            case 1 -> n.heuristicaManhattan(obj);
            case 2 -> n.heuristicaConflictoLineal(obj);
            case 3 -> n.heuristicaEsquinas(obj);
            default -> n.heuristicaManhattan(obj);
        };
    }
}