package Puzzle24;

import java.util.*;

public class App {

    static final int DIMENSION = 5;
    static final String OBJETIVO = "ABCDEFGHIJKLMNOPQRSTUVWX ";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcionInicial = -1;

        do {
            try {
                System.out.println("\n" + "=".repeat(15) + " 24 PUZZLE - IDA* (5x5) " + "=".repeat(15));
                System.out.println("1) Ingresar estado manual");
                System.out.println("2) Generar estado aleatorio resoluble");
                System.out.println("0) Salir");
                System.out.print("Seleccione una opción: ");

                if (!sc.hasNextInt()) {
                    sc.nextLine();
                    continue;
                }

                opcionInicial = sc.nextInt();
                sc.nextLine();

                if (opcionInicial == 0) break;

                String estadoInicial = "";
                if (opcionInicial == 1) {
                    System.out.println("Ingrese los 25 caracteres:");
                    estadoInicial = sc.nextLine();
                    if (estadoInicial.length() != 25) {
                        System.err.println("Error: Deben ser 25 caracteres.");
                        continue;
                    }
                } else {
                    estadoInicial = generarEstadoMezclado(55);
                }

                imprimirEstado(estadoInicial);

                System.out.println("\n--- Configuración de búsqueda ---");
                System.out.println("1) IDA* Manhattan");
                System.out.println("2) IDA* Manhattan + Conflicto Lineal");
                System.out.println("3) IDA* Manhattan + C. Lineal + Esquinas");
                System.out.println("4) Comparar las tres");
                System.out.print("Opción: ");

                int modo = sc.nextInt();
                sc.nextLine();

                if (modo >= 1 && modo <= 3) {
                    ejecutarIndividual(estadoInicial, modo);
                } else if (modo == 4) {
                    compararHeuristicas(estadoInicial);
                }

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                sc.nextLine();
            }
        } while (opcionInicial != 0);
        sc.close();
    }

    private static void ejecutarIndividual(String estado, int tipo) {
        Arbol arbol = new Arbol(new Nodo(estado));
        long inicio = System.nanoTime();
        Nodo solucion = arbol.IDAStar(OBJETIVO, tipo);
        long fin = System.nanoTime();

        double tiempoMs = (fin - inicio) / 1_000_000.0;
        String[] nombres = {"", "Manhattan", "M + Conflicto", "M + C.L. + Esquinas"};

        System.out.println("\n====== RESULTADO ======");
        System.out.printf("%-20s %-15s %-15s %-15s\n", "Heurística", "Nodos", "Tiempo(ms)", "Movimientos");
        System.out.printf("%-20s %-15d %-15.3f %-15d\n", nombres[tipo], arbol.nodosExplorados, tiempoMs, (solucion != null ? solucion.nivel : -1));

        if (solucion != null) {
            System.out.print("\n¿Ver camino? (1: Si, 0: No): ");
            if (new Scanner(System.in).nextInt() == 1) solucion.imprimirCamino();
        }
    }

    private static void compararHeuristicas(String estado) {
        System.out.println("\nGenerando comparativa (esto puede tardar)...");

        for (int i = 1; i <= 3; i++) {
            Arbol a = new Arbol(new Nodo(estado));
            long inicio = System.nanoTime();
            Nodo s = a.IDAStar(OBJETIVO, i);
            long fin = System.nanoTime();

            String[] nombres = {"", "Manhattan", "M + Conflicto", "M + C.L. + Esquinas"};
            System.out.printf("%-20s | Nodos: %-10d | Tiempo: %-10.2f ms | Movs: %d\n",
                    nombres[i], a.nodosExplorados, (fin - inicio) / 1_000_000.0, (s != null ? s.nivel : -1));
        }
    }

    private static String generarEstadoMezclado(int pasos) {
        Nodo actual = new Nodo(OBJETIVO);
        Random r = new Random();
        for (int i = 0; i < pasos; i++) {
            LinkedList<Nodo> sucesores = actual.generarSucesores();
            actual = sucesores.get(r.nextInt(sucesores.size()));
        }
        return actual.estado;
    }

    private static void imprimirEstado(String estado) {
        System.out.println("Tablero Actual:");
        for (int i = 0; i < estado.length(); i++) {
            System.out.print("[" + (estado.charAt(i) == ' ' ? "_" : estado.charAt(i)) + "] ");
            if ((i + 1) % 5 == 0) System.out.println();
        }
    }
}