/**
 * SimuladorPartidas: enfrenta dos jugadores en N partidas y devuelve estadísticas.
 *
 * Alterna quién empieza en cada partida (la mitad empieza jugador1, la otra mitad jugador2).
 * Registra victorias/derrotas/empates, tiempo medio de búsqueda y nodos generados.
 */
public class SimuladorPartidas {

    /** Resultado de una serie de partidas */
    public static class Resultado {
        public int victoriasJ1 = 0;
        public int victoriasJ2 = 0;
        public int empates     = 0;
        public int totalPartidas;
        public long tiempoTotalMs = 0;

        public Resultado(int totalPartidas) {
            this.totalPartidas = totalPartidas;
        }

        public double porcentajeVictoriasJ1() {
            return 100.0 * victoriasJ1 / totalPartidas;
        }

        public double porcentajeVictoriasJ2() {
            return 100.0 * victoriasJ2 / totalPartidas;
        }

        public double porcentajeEmpates() {
            return 100.0 * empates / totalPartidas;
        }

        public double tiempoMedioMsPorPartida() {
            return (double) tiempoTotalMs / totalPartidas;
        }

        @Override
        public String toString() {
            return String.format(
                "Partidas: %d | Victorias J1: %d (%.1f%%) | Victorias J2: %d (%.1f%%) | Empates: %d (%.1f%%) | Tiempo medio: %.1f ms/partida",
                totalPartidas,
                victoriasJ1, porcentajeVictoriasJ1(),
                victoriasJ2, porcentajeVictoriasJ2(),
                empates,     porcentajeEmpates(),
                tiempoMedioMsPorPartida()
            );
        }
    }

    /**
     * Enfrenta jugador1 contra jugador2 en numPartidas partidas.
     * La mitad de las partidas empieza j1, la otra mitad empieza j2.
     *
     * @param jugador1    Jugador con id=1 y su estrategia ya establecida
     * @param jugador2    Jugador con id=2 y su estrategia ya establecida
     * @param numPartidas Número total de partidas a simular
     * @param silencioso  Si true, no imprime estado de cada partida
     * @return Resultado con estadísticas
     */
    public static Resultado simular(Jugador jugador1, Jugador jugador2,
                                    int numPartidas, boolean silencioso) {
        Resultado resultado = new Resultado(numPartidas);

        for (int partida = 0; partida < numPartidas; partida++) {
            // Alternar quién empieza
            boolean j1Empieza = (partida % 2 == 0);

            Tablero tablero = new Tablero();
            tablero.inicializar();
            tablero.obtenerGanador();

            long inicio = System.currentTimeMillis();
            jugarPartida(jugador1, jugador2, tablero, j1Empieza);
            long fin = System.currentTimeMillis();

            resultado.tiempoTotalMs += (fin - inicio);

            // Registrar ganador
            if (tablero.hayEmpate()) {
                resultado.empates++;
                if (!silencioso)
                    System.out.println("Partida " + (partida + 1) + ": EMPATE");
            } else if (tablero.ganaJ1()) {
                resultado.victoriasJ1++;
                if (!silencioso)
                    System.out.println("Partida " + (partida + 1) + ": Gana J1");
            } else {
                resultado.victoriasJ2++;
                if (!silencioso)
                    System.out.println("Partida " + (partida + 1) + ": Gana J2");
            }
        }

        return resultado;
    }

    /** Juega una partida completa entre jugador1 y jugador2 */
    private static void jugarPartida(Jugador jugador1, Jugador jugador2,
                                      Tablero tablero, boolean j1Empieza) {
        int turno = j1Empieza ? 1 : 2;

        while (!tablero.esFinal()) {
            Jugador jugadorActual = (turno == 1) ? jugador1 : jugador2;

            int movimiento = jugadorActual.obtenerJugada(tablero);

            if (movimiento >= 0 && movimiento < Tablero.NCOLUMNAS) {
                boolean[] posibles = tablero.columnasLibres();
                if (posibles[movimiento]) {
                    tablero.anadirFicha(movimiento, jugadorActual.getIdentificador());
                    tablero.obtenerGanador();
                } else {
                    Conecta4.ERROR_FATAL("Columna completa en simulación. Abortando.");
                    return;
                }
            } else {
                Conecta4.ERROR_FATAL("Movimiento inválido en simulación. Abortando.");
                return;
            }

            turno = Jugador.alternarJugador(turno);
        }
    }
}
