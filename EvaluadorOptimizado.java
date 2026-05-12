import java.util.*;

/**
 * EvaluadorOptimizado: extiende Evaluador usando pesos ajustados
 * automáticamente
 * mediante ascenso a colinas (hill climbing), tal como describe el enunciado.
 *
 * Uso típico:
 * EvaluadorOptimizado eval = new EvaluadorOptimizado();
 * eval.optimizar(); // ajusta los pesos automáticamente
 * // A partir de aquí, valoracion() usa los pesos optimizados.
 *
 * También puede construirse directamente con pesos ya conocidos:
 * EvaluadorOptimizado eval = new EvaluadorOptimizado(pesosYaOptimizados);
 */
public class EvaluadorOptimizado extends Evaluador {

    private static final double VARIACION = 0.1; // 10% de variación por paso
    private static final int NUM_PARTIDAS = 8; // partidas por enfrentamiento
    private static final int PROFUNDIDAD = 4; // profundidad de búsqueda

    private PesosEvaluacion pesos;

    /** Constructor por defecto: pesos uniformes, pendientes de optimizar */
    public EvaluadorOptimizado() {
        this.pesos = new PesosEvaluacion(1.0, 1.0, 1.0, 1.0);
    }

    /**
     * Constructor con pesos ya conocidos (p.ej. resultado previo de optimizar())
     */
    public EvaluadorOptimizado(PesosEvaluacion pesos) {
        this.pesos = pesos;
    }

    // =========================================================
    // FUNCIÓN DE VALORACIÓN (implementa Evaluador)
    // =========================================================

    /**
     * Evalúa el tablero desde la perspectiva de 'jugador' usando los pesos
     * actuales.
     * Pondera 4 rasgos (propios - rivales):
     * 1. Líneas de 2 fichas no bloqueadas
     * 2. Control del centro
     * 3. Líneas de 3 fichas no bloqueadas
     * 4. Amenazas dobles (fork)
     */
    @Override
    public int valoracion(Tablero tablero, int jugador) {
        int oponente = Jugador.alternarJugador(jugador);
        double v = 0;

        v += tablero.contarLineas(jugador, 2) * pesos.pesoLineas2;
        v -= tablero.contarLineas(oponente, 2) * pesos.pesoLineas2;

        v += tablero.contarCentro(jugador) * pesos.pesoCentro;
        v -= tablero.contarCentro(oponente) * pesos.pesoCentro;

        v += tablero.contarTriples(jugador) * pesos.pesoTriples;
        v -= tablero.contarTriples(oponente) * pesos.pesoTriples;

        v += tablero.amenazasDobles(jugador) * pesos.pesoAmenazasDobles;
        v -= tablero.amenazasDobles(oponente) * pesos.pesoAmenazasDobles;

        return (int) v;
    }

    // =========================================================
    // OPTIMIZACIÓN AUTOMÁTICA (hill climbing)
    // =========================================================

    /**
     * Ajusta los pesos internos mediante ascenso a colinas.
     * Modifica this.pesos y devuelve los mejores pesos encontrados.
     *
     * Algoritmo (según enunciado):
     * 1. Partir de pesos uniformes.
     * 2. Generar vecinos: ±10% en cada peso, uno a uno (8 candidatos).
     * 3. Enfrentar cada candidato contra el actual en NUM_PARTIDAS partidas.
     * 4. Si algún candidato gana → sustituir y reiniciar desde el paso 2.
     * 5. Si ninguno mejora → PARAR.
     */
    public PesosEvaluacion optimizar() {
        pesos = new PesosEvaluacion(1.0, 1.0, 1.0, 1.0);
        System.out.println("Iniciando optimización de pesos...");
        System.out.println("Pesos iniciales: " + pesos);

        boolean mejora = true;
        int iteracion = 0;

        while (mejora) {
            mejora = false;
            iteracion++;
            System.out.println("\n=== Iteración " + iteracion + " ===");

            for (PesosEvaluacion candidato : generarVecinos(pesos)) {
                System.out.println("Evaluando: " + candidato);
                int resultado = enfrentar(pesos, candidato);

                if (resultado > 0) {
                    // Candidato gana más partidas → adoptarlo
                    System.out.println("  → MEJORA. Nuevos pesos adoptados.");
                    pesos = candidato;
                    mejora = true;
                    break; // reiniciar búsqueda desde el nuevo punto
                } else if (resultado == 0) {
                    System.out.println("  → Empate. Se mantiene el actual.");
                } else {
                    System.out.println("  → Peor. Descartado.");
                }
            }
        }

        System.out.println("\nOptimización terminada. Mejores pesos: " + pesos);
        return pesos;
    }

    /** Devuelve los pesos actuales (útil tras llamar a optimizar()) */
    public PesosEvaluacion getPesos() {
        return pesos;
    }

    // =========================================================
    // MÉTODOS PRIVADOS DE APOYO
    // =========================================================

    /**
     * Genera los 8 vecinos del conjunto de pesos actual:
     * para cada uno de los 4 pesos, una variante +10% y otra -10%.
     */
    private List<PesosEvaluacion> generarVecinos(PesosEvaluacion base) {
        List<PesosEvaluacion> vecinos = new ArrayList<>();
        double f = 1 + VARIACION, d = 1 - VARIACION;

        vecinos.add(
                new PesosEvaluacion(base.pesoLineas2 * f, base.pesoCentro, base.pesoTriples, base.pesoAmenazasDobles));
        vecinos.add(
                new PesosEvaluacion(base.pesoLineas2 * d, base.pesoCentro, base.pesoTriples, base.pesoAmenazasDobles));
        vecinos.add(
                new PesosEvaluacion(base.pesoLineas2, base.pesoCentro * f, base.pesoTriples, base.pesoAmenazasDobles));
        vecinos.add(
                new PesosEvaluacion(base.pesoLineas2, base.pesoCentro * d, base.pesoTriples, base.pesoAmenazasDobles));
        vecinos.add(
                new PesosEvaluacion(base.pesoLineas2, base.pesoCentro, base.pesoTriples * f, base.pesoAmenazasDobles));
        vecinos.add(
                new PesosEvaluacion(base.pesoLineas2, base.pesoCentro, base.pesoTriples * d, base.pesoAmenazasDobles));
        vecinos.add(
                new PesosEvaluacion(base.pesoLineas2, base.pesoCentro, base.pesoTriples, base.pesoAmenazasDobles * f));
        vecinos.add(
                new PesosEvaluacion(base.pesoLineas2, base.pesoCentro, base.pesoTriples, base.pesoAmenazasDobles * d));

        return vecinos;
    }

    /**
     * Enfrenta dos conjuntos de pesos en NUM_PARTIDAS partidas alternando quién
     * empieza.
     * 'base' juega como J1, 'candidato' como J2.
     *
     * @return > 0 si candidato (J2) gana más → mejora
     *         = 0 si empatan en victorias
     *         < 0 si base (J1) gana más → no mejora
     */
    private int enfrentar(PesosEvaluacion base, PesosEvaluacion candidato) {
        Jugador j1 = new Jugador(1);
        Jugador j2 = new Jugador(2);

        j1.establecerEstrategia(new EstrategiaAlfaBetha(
                new EvaluadorOptimizado(base), PROFUNDIDAD));
        j2.establecerEstrategia(new EstrategiaAlfaBetha(
                new EvaluadorOptimizado(candidato), PROFUNDIDAD));

        SimuladorPartidas.Resultado r = SimuladorPartidas.simular(j1, j2, NUM_PARTIDAS, true);

        return r.victoriasJ2 - r.victoriasJ1; // positivo → candidato (J2) ganó más
    }
}