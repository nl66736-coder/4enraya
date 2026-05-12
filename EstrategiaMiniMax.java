public class EstrategiaMiniMax extends Estrategia {
    /*
     * Estrategia que implementa una busqueda MINIMAX
     * 
     * Los parametros de la busqueda (funcion de evaluacion + cota máxima)
     * se establecen al crear el objeto o con las funciones
     * "establecerEvaluador()" y "establecerCapaMaxima()"
     */

    protected Evaluador _evaluador;
    protected int _capaMaxima;
    protected long _nodosGenerados = 0; // nodos expandidos en la última búsqueda
    protected long _tiempoUltimoMs = 0; // ms que tardó la última llamada a buscarMovimiento

    protected int _jugadorMAX; // - guarda el identificador del jugador
                               // que hace el papel de MAX
    // - necesario al hacer las evaluaciones
    // de posiciones finales (ganador, perdedor, empate)
    // en el caso base de la recursividad del MINIMAX

    public long getNodosGenerados() {
        return _nodosGenerados;
    }

    public long getTiempoUltimoMs() {
        return _tiempoUltimoMs;
    }

    /** Creates a new instance of EstrategiaMiniMax */
    public EstrategiaMiniMax() {
    }

    public EstrategiaMiniMax(Evaluador evaluador, int capaMaxima) {
        this.establecerEvaluador(evaluador);
        this.establecerCapaMaxima(capaMaxima);
    }

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
                _nodosGenerados++; // este sucesor se genera aquí
                // crear nuevo tablero y comprobar ganador
                nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();

                // evaluarlo (OJO: cambiar jugador, establecer capa a 1)
                valorSucesor = MINIMAX(nuevoTablero, Jugador.alternarJugador(jugador), 1);
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

    public int MINIMAX(Tablero tablero, int jugador, int capa) {
        // Implementa la propagación de valores MINIMAX propiamente dicha
        // a partir del segundo nivel (capa 1)

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

        // Recursividad sobre los sucesores
        boolean movimientosPosibles[] = tablero.columnasLibres();
        Tablero nuevoTablero;
        int col, valor, valorSucesor;

        if (esCapaMIN(capa)) {
            valor = Evaluador.MAXIMO; // valor máximo
        } else {
            valor = Evaluador.MINIMO; // valor mínimo
        }
        for (col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) { // se puede añadir ficha en columna
                // crear nuevo tablero y comprobar ganador
                nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();

                // evaluarlo (OJO: cambiar jugador e incrementar capa)
                valorSucesor = MINIMAX(nuevoTablero, Jugador.alternarJugador(jugador), (capa + 1));
                nuevoTablero = null; // Ya no se necesita
                // tomar minimo o maximo
                if (esCapaMIN(capa)) {
                    valor = minimo2(valor, valorSucesor);
                } else {
                    valor = maximo2(valor, valorSucesor);
                }
            }
        }
        return (valor);
    }

    public void establecerCapaMaxima(int capaMaxima) {
        _capaMaxima = capaMaxima;
    }

    public void establecerEvaluador(Evaluador evaluador) {
        _evaluador = evaluador;
    }

    protected static final boolean esCapaMIN(int capa) {
        return ((capa % 2) == 1); // es impar
    }

    protected static final boolean esCapaMAX(int capa) {
        return ((capa % 2) == 0); // es par
    }

    private static final int maximo2(int v1, int v2) {
        if (v1 > v2)
            return (v1);
        else
            return (v2);
    }

    private static final int minimo2(int v1, int v2) {
        if (v1 < v2)
            return (v1);
        else
            return (v2);
    }

} // Fin clase EstartegiaMINIMAX
