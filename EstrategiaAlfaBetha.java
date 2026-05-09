public class EstrategiaAlfaBetha extends EstrategiaMiniMax {

    public EstrategiaAlfaBetha() {}

    public EstrategiaAlfaBetha(Evaluador evaluador, int capaMaxima) {
        super(evaluador, capaMaxima);
    }

    @Override
    public int buscarMovimiento(Tablero tablero, int jugador) {
        _nodosGenerados = 0;                          // resetear antes de buscar
        long inicio = System.currentTimeMillis();

        boolean[] movimientosPosibles = tablero.columnasLibres();
        Tablero nuevoTablero;
        int col, valorSucesor;
        int mejorPosicion = -1;
        int mejorValor    = Evaluador.MINIMO;

        _jugadorMAX = jugador;

        for (col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) {
                _nodosGenerados++;                    // nodo raíz generado
                nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();

                valorSucesor = ALPHABETHA(nuevoTablero, Jugador.alternarJugador(jugador),
                                          1, mejorValor, Evaluador.MAXIMO);
                nuevoTablero = null;

                if (valorSucesor >= mejorValor) {
                    mejorValor    = valorSucesor;
                    mejorPosicion = col;
                }
            }
        }

        _tiempoUltimoMs = System.currentTimeMillis() - inicio;
        return mejorPosicion;
    }

    public int ALPHABETHA(Tablero tablero, int jugador, int capa, int alpha, int betha) {
        if (tablero.hayEmpate())                                     return 0;
        if (tablero.esGanador(_jugadorMAX))                          return Evaluador.MAXIMO;
        if (tablero.esGanador(Jugador.alternarJugador(_jugadorMAX))) return Evaluador.MINIMO;
        if (capa == _capaMaxima)                                     return _evaluador.valoracion(tablero, _jugadorMAX);

        if (esCapaMIN(capa)) return ALPHABETHAMIN(tablero, jugador, capa, alpha, betha);
        else                 return ALPHABETHAMAX(tablero, jugador, capa, alpha, betha);
    }

    private int ALPHABETHAMIN(Tablero tablero, int jugador, int capa, int alpha, int betha) {
        int bethaActual = betha;
        int vActual     = Evaluador.MAXIMO;
        boolean[] movimientosPosibles = tablero.columnasLibres();

        for (int col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) {
                _nodosGenerados++;                    // contar nodo generado
                Tablero nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();

                int aux = ALPHABETHA(nuevoTablero, Jugador.alternarJugador(jugador),
                                     capa + 1, alpha, bethaActual);
                if (vActual > aux) {
                    vActual     = aux;
                    bethaActual = aux;
                }
                if (alpha >= bethaActual) break;      // poda alfa
            }
        }
        return vActual;
    }

    private int ALPHABETHAMAX(Tablero tablero, int jugador, int capa, int alpha, int betha) {
        int alphaActual = alpha;
        int vActual     = Evaluador.MINIMO;
        boolean[] movimientosPosibles = tablero.columnasLibres();

        for (int col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) {
                _nodosGenerados++;                    // contar nodo generado
                Tablero nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();

                int aux = ALPHABETHA(nuevoTablero, Jugador.alternarJugador(jugador),
                                     capa + 1, alphaActual, betha);
                if (vActual < aux) {
                    vActual     = aux;
                    alphaActual = vActual;
                }
                if (betha <= alphaActual) break;      // poda beta
            }
        }
        return vActual;
    }
}
