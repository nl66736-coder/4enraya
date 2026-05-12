public class EstrategiaAlfaBetha extends EstrategiaMiniMax {
    /*
     * Estrategia que implementa una busqueda MINIMAX
     * 
     * Los parametros de la busqueda (funcion de evaluacion + cota máxima)
     * se establecen al crear el objeto o con las funciones
     * "establecerEvaluador()" y "establecerCapaMaxima()"
     */

    /** Creates a new instance of EstrategiaMiniMax */
    public EstrategiaAlfaBetha() {
    }

    public EstrategiaAlfaBetha(Evaluador evaluador, int capaMaxima) {
        super(evaluador, capaMaxima);
    }

    @Override
    public int buscarMovimiento(Tablero tablero, int jugador) {
        // Implementa primera capa del MINIMAX + seleccion jugada mas prometedora
        //
        //
        // capa O -> capa MAX -> maximiza
        // devuelve la columna con mayor evaluacion

        _nodosGenerados = 0; // resetear antes de buscar
        long inicio = System.currentTimeMillis();
        boolean movimientosPosibles[] = tablero.columnasLibres();
        Tablero nuevoTablero;
        int col, valorSucesor;
        int mejorPosicion = -1; // Movimiento nulo
        int mejorValor = Evaluador.MINIMO; // Minimo valor posible

        _jugadorMAX = jugador; // - anota el identificador del jugador que
                               // tiene el papel de MAX
                               // - necesario para evaluar posiciones finales
        for (col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) { // se puede añadir ficha en columna
                _nodosGenerados++; // nodo raíz generado
                // crear nuevo tablero y comprobar ganador
                nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();

                // evaluarlo (OJO: cambiar jugador, establecer capa a 1)
                valorSucesor = ALPHABETHA(nuevoTablero, Jugador.alternarJugador(jugador), 1, Evaluador.MINIMO,
                        Evaluador.MAXIMO);
                nuevoTablero = null; // Ya no se necesita

                // tomar mejor valor
                if (valorSucesor >= mejorValor) {
                    mejorValor = valorSucesor;
                    mejorPosicion = col;
                }
            }
        }

        _tiempoUltimoMs = System.currentTimeMillis() - inicio;
        return (mejorPosicion);
    }

    public int ALPHABETHA(Tablero tablero, int jugador, int capa, int alpha, int betha) {
        // Casos base
        if (tablero.hayEmpate()) {
            return (0);
        }
        // la evaluacion de posiciones finales (caso base recursididad)
        // se hace SIEMPRE desde la prespectiva de MAX
        // -> se usa el identificador del jugador MAX (1 o 2) guardado
        // en la llamada a buscarMovimiento()
        if (tablero.esGanador(_jugadorMAX)) { // gana MAX
            return (Evaluador.MAXIMO);
        }
        if (tablero.esGanador(Jugador.alternarJugador(_jugadorMAX))) { // gana el otro
            return (Evaluador.MINIMO);
        }
        if (capa == (_capaMaxima)) { // alcanza nivel maximo
            return (_evaluador.valoracion(tablero, _jugadorMAX));
        }

        if (esCapaMIN(capa)) {
            return ALPHABETHAMIN(tablero, jugador, capa, alpha, betha);
        } else {
            return ALPHABETHAMAX(tablero, jugador, capa, alpha, betha);
        }

    }

    private int ALPHABETHAMIN(Tablero tablero, int jugador, int capa, int alpha, int betha) {
        int bethaActual = betha;
        int vActual = Evaluador.MAXIMO;
        boolean movimientosPosibles[] = tablero.columnasLibres();
        Tablero nuevoTablero;
        for (int col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) { // se puede añadir ficha en columna
                _nodosGenerados++; // contar nodo generado
                nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();
                int aux = ALPHABETHA(nuevoTablero, Jugador.alternarJugador(jugador), (capa + 1), alpha, bethaActual);
                if (vActual > aux) {
                    vActual = aux;
                    bethaActual = aux;
                }
                if (alpha >= bethaActual) {
                    break;
                }
            }
        }
        return vActual;
    }

    private int ALPHABETHAMAX(Tablero tablero, int jugador, int capa, int alpha, int betha) {
        int alphaActual = alpha;
        int vActual = Evaluador.MINIMO;
        boolean movimientosPosibles[] = tablero.columnasLibres();
        Tablero nuevoTablero;
        for (int col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) { // se puede añadir ficha en columna
                _nodosGenerados++; // contar nodo generado
                nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();
                int aux = ALPHABETHA(nuevoTablero, Jugador.alternarJugador(jugador), (capa + 1), alphaActual, betha);
                if (vActual < aux) {
                    vActual = aux;
                    alphaActual = vActual;
                }
                if (betha <= alphaActual) {
                    break;
                }
            }
        }
        return vActual;
    }

} // Fin clase EstartegiaALPHABETHA
