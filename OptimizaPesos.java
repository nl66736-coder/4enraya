/**
 * OptimizaPesos: ajuste automático de pesos mediante ascenso a colinas (hill climbing).
 *
 * Algoritmo (según enunciado):
 * 1. Partir de pesos uniformes (todos a 1.0).
 * 2. En cada iteración generar vecinos: incrementar/decrementar un 10% cada peso, uno a uno.
 * 3. Enfrentar cada candidato contra el actual en NUM_PARTIDAS partidas.
 * 4. Si el candidato gana más partidas → sustituye al actual y se reinicia la búsqueda.
 * 5. Si ningún vecino mejora al actual → PARAR.
 */
public class OptimizaPesos {

    private static final double VARIACION     = 0.1;  // 10% de variación por paso
    private static final int    NUM_PARTIDAS  = 8;    // partidas por enfrentamiento
    private static final int    PROFUNDIDAD   = 4;    // profundidad de búsqueda

    /**
     * Ejecuta el ajuste automático de pesos y devuelve los mejores pesos encontrados.
     */
    public PesosEvaluacion ajustar() {
        PesosEvaluacion actual = new PesosEvaluacion(1.0, 1.0, 1.0, 1.0);
        System.out.println("Iniciando optimización de pesos...");
        System.out.println("Pesos iniciales: " + actual);

        boolean mejora = true;
        int iteracion = 0;

        while (mejora) {
            mejora = false;
            iteracion++;
            System.out.println("\n=== Iteración " + iteracion + " ===");

            for (PesosEvaluacion candidato : generarVecinos(actual)) {
                System.out.println("Evaluando: " + candidato);
                int resultado = enfrentar(actual, candidato);
                // resultado > 0 → candidato gana más partidas
                if (resultado > 0) {
                    System.out.println("  → MEJORA encontrada. Nuevo conjunto de pesos adoptado.");
                    actual = candidato;
                    mejora = true;
                    break; // reiniciar búsqueda desde el nuevo punto
                } else if (resultado == 0) {
                    System.out.println("  → Empate. Se mantiene el conjunto actual.");
                } else {
                    System.out.println("  → Peor. Descartado.");
                }
            }
        }

        System.out.println("\nOptimización terminada. Mejor conjunto de pesos:");
        System.out.println(actual);
        return actual;
    }

    /**
     * Genera los vecinos del conjunto de pesos actual.
     * Para cada uno de los 4 pesos se generan dos variantes: +10% y -10%.
     * Total: 8 candidatos.
     */
    private java.util.List<PesosEvaluacion> generarVecinos(PesosEvaluacion base) {
        java.util.List<PesosEvaluacion> vecinos = new java.util.ArrayList<>();

        // Peso 1: pesoLineas2
        vecinos.add(new PesosEvaluacion(
                base.pesoLineas2 * (1 + VARIACION), base.pesoCentro,
                base.pesoTriples, base.pesoAmenazasDobles));
        vecinos.add(new PesosEvaluacion(
                base.pesoLineas2 * (1 - VARIACION), base.pesoCentro,
                base.pesoTriples, base.pesoAmenazasDobles));

        // Peso 2: pesoCentro
        vecinos.add(new PesosEvaluacion(
                base.pesoLineas2, base.pesoCentro * (1 + VARIACION),
                base.pesoTriples, base.pesoAmenazasDobles));
        vecinos.add(new PesosEvaluacion(
                base.pesoLineas2, base.pesoCentro * (1 - VARIACION),
                base.pesoTriples, base.pesoAmenazasDobles));

        // Peso 3: pesoTriples
        vecinos.add(new PesosEvaluacion(
                base.pesoLineas2, base.pesoCentro,
                base.pesoTriples * (1 + VARIACION), base.pesoAmenazasDobles));
        vecinos.add(new PesosEvaluacion(
                base.pesoLineas2, base.pesoCentro,
                base.pesoTriples * (1 - VARIACION), base.pesoAmenazasDobles));

        // Peso 4: pesoAmenazasDobles
        vecinos.add(new PesosEvaluacion(
                base.pesoLineas2, base.pesoCentro,
                base.pesoTriples, base.pesoAmenazasDobles * (1 + VARIACION)));
        vecinos.add(new PesosEvaluacion(
                base.pesoLineas2, base.pesoCentro,
                base.pesoTriples, base.pesoAmenazasDobles * (1 - VARIACION)));

        return vecinos;
    }

    /**
     * Enfrenta dos conjuntos de pesos en NUM_PARTIDAS partidas.
     * El jugador con pesos 'base' es siempre J1; el 'candidato' es J2.
     * Alterna quién empieza cada partida.
     *
     * @return  > 0 si candidato gana más partidas que base
     *          = 0 si empatan
     *          < 0 si base gana más partidas que candidato
     */
    private int enfrentar(PesosEvaluacion base, PesosEvaluacion candidato) {
        Jugador j1 = new Jugador(1);
        Jugador j2 = new Jugador(2);

        j1.establecerEstrategia(new EstrategiaAlfaBetha(
                new EvaluadorPonderado(base), PROFUNDIDAD));
        j2.establecerEstrategia(new EstrategiaAlfaBetha(
                new EvaluadorPonderado(candidato), PROFUNDIDAD));

        SimuladorPartidas.Resultado resultado =
                SimuladorPartidas.simular(j1, j2, NUM_PARTIDAS, true);

        // candidato es J2: comparamos sus victorias con las del base (J1)
        return resultado.victoriasJ2 - resultado.victoriasJ1;
    }
}
