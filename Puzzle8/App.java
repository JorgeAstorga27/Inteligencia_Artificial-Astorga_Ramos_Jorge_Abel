package Puzzle8;

public class App {
    public static void main(String[] args) throws Exception {
        Nodo raiz = new Nodo("1238 4765");
        Arbol puzzle = new Arbol(raiz);

        String objetivo = "1284376 5";

        System.out.println("--- Iniciando Búsqueda por Profundidad (DFS) ---");
        Nodo nAnchura = puzzle.BusquedaAnchura(objetivo);
        Nodo n = puzzle.BusquedaProfundidad(objetivo);
        Nodo nCostoUniforme = puzzle.BusquedaCostoUniforme(objetivo);
        n.imprimirCamino();
        System.out.println("*******************************************+");
        nAnchura.imprimirCamino();
        System.out.println("*******************************************+");
        nCostoUniforme.imprimirCamino();

        System.out.println("Profundidad de la solución (Nivel): " + n.nivel);
        System.out.println("Nivel en Anchura: " + nAnchura.nivel);
        System.out.println("Nivel en Costo Uniforme: " +  nCostoUniforme.nivel);
    }
}