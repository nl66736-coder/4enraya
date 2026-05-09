public class Tablero {

    public static final int NFILAS    = 6;
    public static final int NCOLUMNAS = 7;
    public static final int NOBJETIVO = 4;

    private static final String MARCA_J1    = "X";
    private static final String MARCA_J2    = "O";
    private static final String MARCA_VACIO = " ";
    private static final String[] MARCAS    = {MARCA_VACIO, MARCA_J1, MARCA_J2};

    private static final int VACIO   = 0;
    private static final int JUGADOR1 = 1;
    private static final int JUGADOR2 = 2;
    private static final int EMPATE  = -1;

    private int[][] _casillas;
    private int[]   _posicionLibre;
    private int     _ganador = EMPATE;

    /** Creates a new instance of Tablero */
    public Tablero() {
        this._casillas      = new int[NCOLUMNAS][NFILAS];
        this._posicionLibre = new int[NCOLUMNAS];
        this.inicializar();
    }

    protected Object clone() {
        Tablero result = new Tablero();
        result.copiarCasillas(this._casillas);
        result.copiarPosicionLibre(this._posicionLibre);
        result._ganador = this._ganador;
        return result;
    }

    protected void finalize() {
        this._casillas      = null;
        this._posicionLibre = null;
    }

    public boolean equals(Object obj) {
        Tablero tablero = (Tablero) obj;
        return tablero.casillasIguales(_casillas);
    }

    public String toString() {
        String result = new String();
        for (int fila = NFILAS - 1; fila >= 0; fila--) {
            result += "|";
            for (int col = 0; col < NCOLUMNAS; col++) {
                result += (MARCAS[_casillas[col][fila]] + "|");
            }
            result += "\n";
        }
        return result;
    }

    public boolean[] columnasLibres() {
        boolean[] result = new boolean[NCOLUMNAS];
        for (int col = 0; col < NCOLUMNAS; col++) {
            result[col] = (_posicionLibre[col] < NFILAS);
        }
        return result;
    }

    boolean esFinal() {
        return (_ganador != 0);
    }

    public void obtenerGanador() {
        _ganador = 0;
        for (int col = 0; col < NCOLUMNAS; col++) {
            for (int fila = 0; fila < NFILAS; fila++) {
                int jugador = _casillas[col][fila];
                if (jugador != VACIO) {
                    if (hayLineaVertical(col, fila, jugador) ||
                        hayLineaHorizontal(col, fila, jugador) ||
                        hayLineaDiagonal(col, fila, jugador)) {
                        _ganador = jugador;
                        return;
                    }
                }
            }
        }
        // Comprobar empate
        boolean empate = true;
        for (int col = 0; col < NCOLUMNAS; col++) {
            empate = empate && (_posicionLibre[col] == NFILAS);
        }
        if (empate) {
            _ganador = EMPATE;
        }
    }

    void inicializar() {
        for (int col = 0; col < NCOLUMNAS; col++) {
            for (int fila = 0; fila < NFILAS; fila++) {
                _casillas[col][fila] = VACIO;
            }
            _posicionLibre[col] = 0;
        }
        _ganador = 0;
    }

    public void mostrar() {
        System.out.println();
        System.out.print(this.toString());
        System.out.print("_");
        for (int col = 0; col < NCOLUMNAS; col++) {
            System.out.print("__");
        }
        System.out.println();
        System.out.print("|");
        for (int col = 0; col < NCOLUMNAS; col++) {
            System.out.print(col + "|");
        }
        System.out.println();
    }

    public boolean ganaJ1()    { return (_ganador == JUGADOR1); }
    public boolean ganaJ2()    { return (_ganador == JUGADOR2); }
    public boolean hayEmpate() { return (_ganador == EMPATE);   }

    public void anadirFicha(int columna, int jugador) {
        // CORREGIDO: usar NFILAS en vez de NCOLUMNAS-1
        if (_posicionLibre[columna] < NFILAS) {
            _casillas[columna][_posicionLibre[columna]] = jugador;
            _posicionLibre[columna]++;
        }
    }

    private boolean hayLineaVertical(int col, int fila, int jugador) {
        int numCasillas = 0;
        for (int j = fila; j < NFILAS; j++) {
            if (_casillas[col][j] == jugador) numCasillas++;
            else break;
        }
        return (numCasillas >= NOBJETIVO);
    }

    private boolean hayLineaHorizontal(int col, int fila, int jugador) {
        int numCasillas = 0;
        for (int i = col; i < NCOLUMNAS; i++) {
            if (_casillas[i][fila] == jugador) numCasillas++;
            else break;
        }
        return (numCasillas >= NOBJETIVO);
    }

    private boolean hayLineaDiagonal(int col, int fila, int jugador) {
        int numCasillas = 0;
        // diagonal creciente
        for (int k = 0; k < NOBJETIVO; k++) {
            int i = col + k, j = fila + k;
            if (i < NCOLUMNAS && j < NFILAS && _casillas[i][j] == jugador) numCasillas++;
            else break;
        }
        if (numCasillas >= NOBJETIVO) return true;

        // diagonal decreciente
        numCasillas = 0;
        for (int k = 0; k < NOBJETIVO; k++) {
            int i = col + k, j = fila - k;
            if (i < NCOLUMNAS && j >= 0 && _casillas[i][j] == jugador) numCasillas++;
            else break;
        }
        return (numCasillas >= NOBJETIVO);
    }

    private void copiarCasillas(int[][] casillas) {
        for (int col = 0; col < NCOLUMNAS; col++)
            for (int fila = 0; fila < NFILAS; fila++)
                this._casillas[col][fila] = casillas[col][fila];
    }

    private void copiarPosicionLibre(int[] posicionLibre) {
        for (int col = 0; col < NCOLUMNAS; col++)
            this._posicionLibre[col] = posicionLibre[col];
    }

    public boolean casillasIguales(int[][] casillas) {
        for (int col = 0; col < NCOLUMNAS; col++)
            for (int fila = 0; fila < NFILAS; fila++)
                if (this._casillas[col][fila] != casillas[col][fila])
                    return false;
        return true;
    }

    public boolean finalJuego() { return (_ganador != 0); }
    public int     ganador()    { return _ganador; }
    public boolean esGanador(int jugador) { return (jugador == _ganador); }

    // =========================================================
    //  MÉTODOS DE EVALUACIÓN (nuevos)
    // =========================================================

    /**
     * Cuenta líneas de exactamente 'longitud' fichas del jugador dado
     * que aún tienen espacio para crecer (no están bloqueadas por el rival).
     * Se recorren horizontal, vertical y ambas diagonales.
     */
    public int contarLineas(int jugador, int longitud) {
        int count = 0;
        int rival = Jugador.alternarJugador(jugador);

        // Horizontal
        for (int fila = 0; fila < NFILAS; fila++) {
            for (int col = 0; col <= NCOLUMNAS - longitud; col++) {
                count += ventanaLibre(col, fila, 1, 0, longitud, jugador, rival);
            }
        }
        // Vertical
        for (int col = 0; col < NCOLUMNAS; col++) {
            for (int fila = 0; fila <= NFILAS - longitud; fila++) {
                count += ventanaLibre(col, fila, 0, 1, longitud, jugador, rival);
            }
        }
        // Diagonal creciente
        for (int col = 0; col <= NCOLUMNAS - longitud; col++) {
            for (int fila = 0; fila <= NFILAS - longitud; fila++) {
                count += ventanaLibre(col, fila, 1, 1, longitud, jugador, rival);
            }
        }
        // Diagonal decreciente
        for (int col = 0; col <= NCOLUMNAS - longitud; col++) {
            for (int fila = longitud - 1; fila < NFILAS; fila++) {
                count += ventanaLibre(col, fila, 1, -1, longitud, jugador, rival);
            }
        }
        return count;
    }

    /**
     * Comprueba una ventana de 'longitud' celdas en dirección (dx,dy)
     * a partir de (col,fila). Devuelve 1 si contiene exactamente 'longitud'
     * fichas del jugador y ninguna del rival; 0 en caso contrario.
     */
    private int ventanaLibre(int col, int fila, int dx, int dy,
                              int longitud, int jugador, int rival) {
        int fichasJugador = 0;
        for (int k = 0; k < longitud; k++) {
            int c = col + k * dx;
            int f = fila + k * dy;
            int celda = _casillas[c][f];
            if (celda == rival)    return 0; // bloqueada
            if (celda == jugador)  fichasJugador++;
        }
        return (fichasJugador == longitud) ? 1 : 0;
    }

    /**
     * Cuenta fichas del jugador en las columnas centrales (3 centrales).
     * El centro del tablero es estratégicamente más valioso en Conecta-4.
     */
    public int contarCentro(int jugador) {
        int count = 0;
        // Columnas 2, 3, 4 (las tres centrales de 7)
        int[] columnasCentro = {2, 3, 4};
        for (int col : columnasCentro) {
            for (int fila = 0; fila < NFILAS; fila++) {
                if (_casillas[col][fila] == jugador) {
                    // La columna 3 (central) vale doble
                    count += (col == 3) ? 2 : 1;
                }
            }
        }
        return count;
    }

    /**
     * Cuenta líneas de 3 fichas propias sin bloquear (alias semántico
     * para contarLineas con longitud=3, separado para claridad).
     */
    public int contarTriples(int jugador) {
        return contarLineas(jugador, 3);
    }

    /**
     * Cuenta amenazas dobles: posiciones en las que el jugador tiene
     * dos líneas de 3 fichas que comparten una casilla vacía ganadora.
     * Esto representa una situación de "fork" (el rival no puede bloquear ambas).
     */
    public int amenazasDobles(int jugador) {
        int count = 0;
        // Para cada casilla vacía, contar cuántas líneas ganadoras del jugador la usan
        for (int col = 0; col < NCOLUMNAS; col++) {
            for (int fila = 0; fila < NFILAS; fila++) {
                if (_casillas[col][fila] == VACIO) {
                    int lineasGanadoras = contarLineasQueUsanCasilla(col, fila, jugador);
                    if (lineasGanadoras >= 2) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Cuenta cuántas líneas ganadoras potenciales (ventanas de 4 con 3
     * fichas del jugador y la casilla (col,fila) vacía) pasan por esa casilla.
     */
    private int contarLineasQueUsanCasilla(int col, int fila, int jugador) {
        int rival = Jugador.alternarJugador(jugador);
        int lineas = 0;

        int[][] direcciones = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (int[] dir : direcciones) {
            int dx = dir[0], dy = dir[1];
            // Probar todas las ventanas de 4 que incluyan (col,fila)
            for (int inicio = 0; inicio < NOBJETIVO; inicio++) {
                int c0 = col - inicio * dx;
                int f0 = fila - inicio * dy;
                // Verificar que la ventana cabe en el tablero
                int cFin = c0 + (NOBJETIVO - 1) * dx;
                int fFin = f0 + (NOBJETIVO - 1) * dy;
                if (c0 < 0 || c0 >= NCOLUMNAS || f0 < 0 || f0 >= NFILAS) continue;
                if (cFin < 0 || cFin >= NCOLUMNAS || fFin < 0 || fFin >= NFILAS) continue;

                int fichasJugador = 0;
                boolean bloqueada = false;
                for (int k = 0; k < NOBJETIVO; k++) {
                    int c = c0 + k * dx;
                    int f = f0 + k * dy;
                    if (_casillas[c][f] == rival) { bloqueada = true; break; }
                    if (_casillas[c][f] == jugador) fichasJugador++;
                }
                if (!bloqueada && fichasJugador == NOBJETIVO - 1) {
                    lineas++;
                }
            }
        }
        return lineas;
    }
}
