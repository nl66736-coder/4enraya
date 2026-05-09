public class EstrategiaMiniMax extends Estrategia {

    protected Evaluador _evaluador;
    protected int       _capaMaxima;
    protected int       _jugadorMAX;

    // ── Contadores de instrumentación ──────────────────────────────────────
    protected long _nodosGenerados   = 0;   // nodos expandidos en la última búsqueda
    protected long _tiempoUltimoMs   = 0;   // ms que tardó la última llamada a buscarMovimiento

    public long getNodosGenerados() { return _nodosGenerados; }
    public long getTiempoUltimoMs() { return _tiempoUltimoMs; }
    // ───────────────────────────────────────────────────────────────────────

    public EstrategiaMiniMax() {}

    public EstrategiaMiniMax(Evaluador evaluador, int capaMaxima) {
        this.establecerEvaluador(evaluador);
        this.establecerCapaMaxima(capaMaxima);
    }

    public int buscarMovimiento(Tablero tablero, int jugador) {
        _nodosGenerados = 0;                          // resetear antes de buscar
        long inicio = System.currentTimeMillis();

        boolean[] movimientosPosibles = tablero.columnasLibres();
        Tablero nuevoTablero;
        int col, valorSucesor;
        int mejorPosicion = -1;
        int mejorValor    = _evaluador.MINIMO;

        _jugadorMAX = jugador;

        for (col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) {
                _nodosGenerados++;                    // este sucesor se genera aquí
                nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();

                valorSucesor = MINIMAX(nuevoTablero, Jugador.alternarJugador(jugador), 1);
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

    public int MINIMAX(Tablero tablero, int jugador, int capa) {
        // Casos base
        if (tablero.hayEmpate())                                  return 0;
        if (tablero.esGanador(_jugadorMAX))                       return _evaluador.MAXIMO;
        if (tablero.esGanador(Jugador.alternarJugador(_jugadorMAX))) return _evaluador.MINIMO;
        if (capa == _capaMaxima)                                  return _evaluador.valoracion(tablero, _jugadorMAX);

        boolean[] movimientosPosibles = tablero.columnasLibres();
        Tablero nuevoTablero;
        int col, valor, valorSucesor;

        valor = esCapaMIN(capa) ? _evaluador.MAXIMO : _evaluador.MINIMO;

        for (col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) {
                _nodosGenerados++;                    // contar cada nodo generado
                nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();

                valorSucesor = MINIMAX(nuevoTablero, Jugador.alternarJugador(jugador), capa + 1);
                nuevoTablero = null;

                if (esCapaMIN(capa)) valor = minimo2(valor, valorSucesor);
                else                 valor = maximo2(valor, valorSucesor);
            }
        }
        return valor;
    }

    public void establecerCapaMaxima(int capaMaxima) { _capaMaxima = capaMaxima; }
    public void establecerEvaluador(Evaluador evaluador) { _evaluador = evaluador; }

    protected static final boolean esCapaMIN(int capa) { return (capa % 2) == 1; }
    protected static final boolean esCapaMAX(int capa) { return (capa % 2) == 0; }

    private static final int maximo2(int v1, int v2) { return v1 > v2 ? v1 : v2; }
    private static final int minimo2(int v1, int v2) { return v1 < v2 ? v1 : v2; }
}
