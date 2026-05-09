public class Jugador {

    private Estrategia _estrategia;
    private int        _identificador;

    public Jugador() {}

    public Jugador(int identificador) {
        _identificador = identificador;
    }

    public void establecerEstrategia(Estrategia estrategia) {
        _estrategia = estrategia;
    }

    /** Nuevo getter: necesario para leer estadísticas desde SimuladorPartidas */
    public Estrategia getEstrategia() {
        return _estrategia;
    }

    public int obtenerJugada(Tablero tablero) {
        return _estrategia.buscarMovimiento(tablero, _identificador);
    }

    public int getIdentificador() {
        return _identificador;
    }

    public static final int alternarJugador(int jugadorActual) {
        return ((jugadorActual % 2) + 1);
    }
}
