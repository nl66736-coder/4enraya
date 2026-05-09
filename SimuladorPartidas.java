public class SimuladorPartidas {

    public static class Resultado {
        public int  victoriasJ1  = 0;
        public int  victoriasJ2  = 0;
        public int  empates      = 0;
        public int  totalPartidas;
        public long tiempoTotalMs = 0;

        // Nuevos acumuladores para estadísticas por movimiento
        public long nodosJ1Total    = 0;
        public long nodosJ2Total    = 0;
        public long tiempoJ1TotalMs = 0;
        public long tiempoJ2TotalMs = 0;
        public int  movimientosJ1   = 0;
        public int  movimientosJ2   = 0;

        public Resultado(int totalPartidas) { this.totalPartidas = totalPartidas; }

        public double porcentajeVictoriasJ1() { return 100.0 * victoriasJ1 / totalPartidas; }
        public double porcentajeVictoriasJ2() { return 100.0 * victoriasJ2 / totalPartidas; }
        public double porcentajeEmpates()     { return 100.0 * empates     / totalPartidas; }
        public double tiempoMedioMsPorPartida(){ return (double) tiempoTotalMs / totalPartidas; }

        // Medias por movimiento
        public double nodosMediaJ1()   { return movimientosJ1 == 0 ? 0 : (double) nodosJ1Total    / movimientosJ1; }
        public double nodosMediaJ2()   { return movimientosJ2 == 0 ? 0 : (double) nodosJ2Total    / movimientosJ2; }
        public double tiempoMediaJ1Ms(){ return movimientosJ1 == 0 ? 0 : (double) tiempoJ1TotalMs / movimientosJ1; }
        public double tiempoMediaJ2Ms(){ return movimientosJ2 == 0 ? 0 : (double) tiempoJ2TotalMs / movimientosJ2; }

        @Override
        public String toString() {
            return String.format(
                "Partidas: %d | V-J1: %d (%.1f%%) | V-J2: %d (%.1f%%) | Empates: %d (%.1f%%)\n" +
                "  J1 -> %.1f ms/mov | %.1f nodos/mov\n" +
                "  J2 -> %.1f ms/mov | %.1f nodos/mov",
                totalPartidas,
                victoriasJ1, porcentajeVictoriasJ1(),
                victoriasJ2, porcentajeVictoriasJ2(),
                empates,     porcentajeEmpates(),
                tiempoMediaJ1Ms(), nodosMediaJ1(),
                tiempoMediaJ2Ms(), nodosMediaJ2()
            );
        }
    }

    public static Resultado simular(Jugador jugador1, Jugador jugador2,
                                    int numPartidas, boolean silencioso) {
        Resultado resultado = new Resultado(numPartidas);

        for (int partida = 0; partida < numPartidas; partida++) {
            boolean j1Empieza = (partida % 2 == 0);

            Tablero tablero = new Tablero();
            tablero.inicializar();
            tablero.obtenerGanador();

            long inicio = System.currentTimeMillis();
            jugarPartida(jugador1, jugador2, tablero, j1Empieza, resultado);
            resultado.tiempoTotalMs += System.currentTimeMillis() - inicio;

            if (tablero.hayEmpate()) {
                resultado.empates++;
                if (!silencioso) System.out.println("Partida " + (partida + 1) + ": EMPATE");
            } else if (tablero.ganaJ1()) {
                resultado.victoriasJ1++;
                if (!silencioso) System.out.println("Partida " + (partida + 1) + ": Gana J1");
            } else {
                resultado.victoriasJ2++;
                if (!silencioso) System.out.println("Partida " + (partida + 1) + ": Gana J2");
            }
        }
        return resultado;
    }

    private static void jugarPartida(Jugador jugador1, Jugador jugador2,
                                      Tablero tablero, boolean j1Empieza,
                                      Resultado resultado) {
        int turno = j1Empieza ? 1 : 2;

        while (!tablero.esFinal()) {
            Jugador jugadorActual = (turno == 1) ? jugador1 : jugador2;

            int movimiento = jugadorActual.obtenerJugada(tablero);

            // Recoger estadísticas si la estrategia las expone
            Estrategia est = jugadorActual.getEstrategia();
            if (est instanceof EstrategiaMiniMax) {
                EstrategiaMiniMax em = (EstrategiaMiniMax) est;
                if (turno == 1) {
                    resultado.nodosJ1Total    += em.getNodosGenerados();
                    resultado.tiempoJ1TotalMs += em.getTiempoUltimoMs();
                    resultado.movimientosJ1++;
                } else {
                    resultado.nodosJ2Total    += em.getNodosGenerados();
                    resultado.tiempoJ2TotalMs += em.getTiempoUltimoMs();
                    resultado.movimientosJ2++;
                }
            }

            if (movimiento >= 0 && movimiento < Tablero.NCOLUMNAS) {
                boolean[] posibles = tablero.columnasLibres();
                if (posibles[movimiento]) {
                    tablero.anadirFicha(movimiento, jugadorActual.getIdentificador());
                    tablero.obtenerGanador();
                } else {
                    Conecta4.ERROR_FATAL("Columna completa en simulación.");
                    return;
                }
            } else {
                Conecta4.ERROR_FATAL("Movimiento inválido en simulación.");
                return;
            }

            turno = Jugador.alternarJugador(turno);
        }
    }
}
