package Puzzle8;

import java.util.*;

public class Arbol {
    Nodo raiz;
    public int nodosExplorados;

    public Arbol(Nodo raiz) {
        this.raiz = raiz;
    }

    Nodo BusquedaAnchura(String estadoObjetivo) {
        nodosExplorados = 0;
        if (raiz == null) return null;

        HashSet<String> visitados = new HashSet<>();
        Queue<Nodo> cola = new LinkedList<>();
        cola.add(raiz);
        visitados.add(raiz.estado);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            nodosExplorados++;
            if (actual.estado.equals(estadoObjetivo)) return actual;

            for (Nodo hijo : actual.generarSucesores()) {
                if (!visitados.contains(hijo.estado)) {
                    visitados.add(hijo.estado);
                    cola.add(hijo);
                }
            }
        }
        return null;
    }

    Nodo BusquedaProfundidad(String estadoObjetivo) {
        nodosExplorados = 0;
        if (raiz == null) return null;

        HashSet<String> visitados = new HashSet<>();
        Stack<Nodo> pila = new Stack<>();
        pila.push(raiz);
        visitados.add(raiz.estado);

        while (!pila.isEmpty()) {
            Nodo actual = pila.pop();
            nodosExplorados++;
            if (actual.estado.equals(estadoObjetivo)) return actual;

            for (Nodo hijo : actual.generarSucesores()) {
                if (!visitados.contains(hijo.estado)) {
                    visitados.add(hijo.estado);
                    pila.push(hijo);
                }
            }
        }
        return null;
    }

    Nodo BusquedaCostoUniforme(String estadoObjetivo) {
        nodosExplorados = 0;
        if (raiz == null) return null;

        HashSet<String> visitados = new HashSet<>();
        PriorityQueue<Nodo> frontera = new PriorityQueue<>();
        raiz.costo = 0;
        frontera.add(raiz);

        while (!frontera.isEmpty()) {
            Nodo actual = frontera.poll();
            if (!visitados.contains(actual.estado)) {
                nodosExplorados++;
                visitados.add(actual.estado);
                if (actual.estado.equals(estadoObjetivo)) return actual;

                for (Nodo hijo : actual.generarSucesores()) {
                    if (!visitados.contains(hijo.estado)) {
                        hijo.costo = actual.costo + 1;
                        frontera.add(hijo);
                    }
                }
            }
        }
        return null;
    }

    Nodo BusquedaEsquinas(String estadoObjetivo) {
        nodosExplorados = 0;
        if (raiz == null) return null;

        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        HashSet<String> cerrados = new HashSet<>();
        raiz.costo = raiz.nivel + raiz.heuristicaEsquinas(estadoObjetivo);
        abiertos.add(raiz);

        while (!abiertos.isEmpty()) {
            Nodo actual = abiertos.poll();
            nodosExplorados++;
            if (actual.estado.equals(estadoObjetivo)) return actual;

            cerrados.add(actual.estado);
            for (Nodo hijo : actual.generarSucesores()) {
                if (!cerrados.contains(hijo.estado)) {
                    hijo.costo = hijo.nivel + hijo.heuristicaEsquinas(estadoObjetivo);
                    abiertos.add(hijo);
                }
            }
        }
        return null;
    }
}