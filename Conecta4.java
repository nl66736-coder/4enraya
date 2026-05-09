public class Conecta4 {

    public static void main(String[] args) {

        // --- MODO 1: Batería de tests automática (para la memoria) ---
        // BateriaTests.ejecutar();

        // --- MODO 2: Partida humano vs máquina (para la defensa) ---
        Jugador jugador1 = new Jugador(1);
        jugador1.establecerEstrategia(new EstrategiaHumano());
        DEBUG("Jugador 1: humano\n");

        Jugador jugador2 = new Jugador(2);
        jugador2.establecerEstrategia(new EstrategiaAlfaBetha(new EvaluadorPonderado(), 4));
        DEBUG("Jugador 2: maquina (alphabeta con eval. ponderado + prof. 4)\n");

        Tablero tablero = new Tablero();
        tablero.inicializar();
        jugar(jugador1, jugador2, tablero);

        // Mostrar resultado final
        tablero.mostrar();
        if (tablero.hayEmpate())  System.out.println("RESULTADO: Empate.");
        if (tablero.ganaJ1())     System.out.println("RESULTADO: Gana jugador 1.");
        if (tablero.ganaJ2())     System.out.println("RESULTADO: Gana jugador 2.");

        System.exit(1);
    }

    private static void jugar(Jugador jugador1, Jugador jugador2, Tablero tablero) {
        int turno = 0;
        Jugador jugadorActual;
        int movimiento;
        boolean[] posicionesPosibles;

        tablero.obtenerGanador();
        while (!tablero.esFinal()) {
            turno++;
            if ((turno % 2) == 1) {
                jugadorActual = jugador1;
            } else {
                jugadorActual = jugador2;
            }

            movimiento = jugadorActual.obtenerJugada(tablero);
          

        // Ver nodos y tiempo del último movimiento
        Estrategia est = jugadorActual.getEstrategia();
        if (est instanceof EstrategiaMiniMax) {
            EstrategiaMiniMax em = (EstrategiaMiniMax) est;
            System.out.printf("  [J%d] nodos: %d | tiempo: %d ms%n",
                jugadorActual.getIdentificador(),
                em.getNodosGenerados(),
                em.getTiempoUltimoMs());
        }

            if ((movimiento >= 0) && (movimiento < Tablero.NCOLUMNAS)) {
                posicionesPosibles = tablero.columnasLibres();
                if (posicionesPosibles[movimiento]) {
                    tablero.anadirFicha(movimiento, jugadorActual.getIdentificador());
                    tablero.obtenerGanador();
                } else {
                    ERROR_FATAL("Columna completa. Juego Abortado.");
                }
            } else {
                ERROR_FATAL("Movimiento invalido. Juego Abortado.");
            }
        }
    }

    private static void cargarArgumentos(String[] args) {
        // procesar parametros de linea de comandos
    }

    public static final void ERROR_FATAL(String mensaje) {
        System.out.println("ERROR FATAL\n\t" + mensaje);
        System.exit(0);
    }

    public static final void DEBUG(String str) {
        System.out.print("DBG:" + str);
    }

    public static final void ERROR(String mensaje) {
        System.out.println("ERROR\n\t" + mensaje);
    }
}
