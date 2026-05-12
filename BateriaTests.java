/**
 * BateriaTests.java
 *
 * Ejecuta todos los enfrentamientos requeridos por la práctica
 * y muestra los resultados en tablas para la memoria.
 *
 * 1. AlphaBeta + heurística optimizada VS AlphaBeta + heurística inicial
 * 2. AlphaBeta + heurística optimizada VS AlphaBeta + heurística aleatoria
 * 3. AlphaBeta + heurística optimizada VS MiniMax + heurística optimizada
 *
 * Para cada enfrentamiento se prueban profundidades 1, 2, 3 y 4.
 * Cada enfrentamiento usa 10 partidas (50% empieza J1, 50% empieza J2).
 */
public class BateriaTests {

    private static final int NUM_PARTIDAS = 10;
    private static final int[] PROFUNDIDADES = { 1, 2, 3, 4 };

    public static void ejecutar() {

        // Paso 1: calcular pesos optimizados una sola vez
        System.out.println(">>> Optimizando pesos...");
        OptimizaPesos op = new OptimizaPesos();
        PesosEvaluacion pesosOpt = op.ajustar();
        System.out.println("Pesos optimizados: " + pesosOpt);
        System.out.println();

        // Paso 2: ejecutar los tres bloques
        enfrentamiento1(pesosOpt);
        enfrentamiento2(pesosOpt);
        enfrentamiento3(pesosOpt);
    }

    // =========================================================
    // ENFRENTAMIENTO 1: AB+Optimizado VS AB+Inicial
    // =========================================================
    private static void enfrentamiento1(PesosEvaluacion pesosOpt) {
        System.out.println("=== ENFRENTAMIENTO 1: AlphaBeta+Optimizado VS AlphaBeta+Inicial ===");
        imprimirCabecera();

        for (int prof : PROFUNDIDADES) {
            Jugador j1 = new Jugador(1);
            Jugador j2 = new Jugador(2);
            j1.establecerEstrategia(new EstrategiaAlfaBetha(new EvaluadorOptimizado(pesosOpt), prof));
            j2.establecerEstrategia(new EstrategiaAlfaBetha(new EvaluadorPonderado(), prof));

            SimuladorPartidas.Resultado r = SimuladorPartidas.simular(j1, j2, NUM_PARTIDAS, true);
            imprimirFila(prof, "AB+Opt", "AB+Ini", r);
        }
        System.out.println();
    }

    // =========================================================
    // ENFRENTAMIENTO 2: AB+Optimizado VS AB+Aleatorio
    // =========================================================
    private static void enfrentamiento2(PesosEvaluacion pesosOpt) {
        System.out.println("=== ENFRENTAMIENTO 2: AlphaBeta+Optimizado VS AlphaBeta+Aleatorio ===");
        imprimirCabecera();

        for (int prof : PROFUNDIDADES) {
            Jugador j1 = new Jugador(1);
            Jugador j2 = new Jugador(2);
            j1.establecerEstrategia(new EstrategiaAlfaBetha(new EvaluadorOptimizado(pesosOpt), prof));
            j2.establecerEstrategia(new EstrategiaAlfaBetha(new EvaluadorAleatorio(), prof));

            SimuladorPartidas.Resultado r = SimuladorPartidas.simular(j1, j2, NUM_PARTIDAS, true);
            imprimirFila(prof, "AB+Opt", "AB+Ale", r);
        }
        System.out.println();
    }

    // =========================================================
    // ENFRENTAMIENTO 3: AB+Optimizado VS MiniMax+Optimizado
    // =========================================================
    private static void enfrentamiento3(PesosEvaluacion pesosOpt) {
        System.out.println("=== ENFRENTAMIENTO 3: AlphaBeta+Optimizado VS MiniMax+Optimizado ===");
        System.out.println("  (AVISO: profundidad 4 con MiniMax puede tardar varios minutos)");
        imprimirCabecera();

        for (int prof : PROFUNDIDADES) {
            Jugador j1 = new Jugador(1);
            Jugador j2 = new Jugador(2);
            j1.establecerEstrategia(new EstrategiaAlfaBetha(new EvaluadorOptimizado(pesosOpt), prof));
            j2.establecerEstrategia(new EstrategiaMiniMax(new EvaluadorOptimizado(pesosOpt), prof));

            SimuladorPartidas.Resultado r = SimuladorPartidas.simular(j1, j2, NUM_PARTIDAS, true);
            imprimirFila(prof, "AB+Opt", "MM+Opt", r);
        }
        System.out.println();
    }

    // =========================================================
    // HELPERS DE FORMATO
    // =========================================================
    private static void imprimirCabecera() {
        System.out.println("-".repeat(90));
        System.out.printf("%-4s | %-8s | %-8s | %7s | %7s | %6s | %10s | %10s | %10s | %10s%n",
                "Prof", "J1", "J2",
                "V-J1(%)", "V-J2(%)", "Emp(%)",
                "ms/mov J1", "nod/mov J1",
                "ms/mov J2", "nod/mov J2");
        System.out.println("-".repeat(90));
    }

    private static void imprimirFila(int prof, String nombreJ1, String nombreJ2,
            SimuladorPartidas.Resultado r) {
        System.out.printf("%-4d | %-8s | %-8s | %7.1f | %7.1f | %6.1f | %10.2f | %10.1f | %10.2f | %10.1f%n",
                prof, nombreJ1, nombreJ2,
                r.porcentajeVictoriasJ1(),
                r.porcentajeVictoriasJ2(),
                r.porcentajeEmpates(),
                r.tiempoMediaJ1Ms(),
                r.nodosMediaJ1(),
                r.tiempoMediaJ2Ms(),
                r.nodosMediaJ2());
    }
}