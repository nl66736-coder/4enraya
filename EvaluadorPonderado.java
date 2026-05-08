public class EvaluadorPonderado extends Evaluador {
    /*
     * Evaluador que pondera cuatro rasgos del tablero:
     *   1. Lineas de 2 fichas propias no bloqueadas
     *   2. Control del centro del tablero
     *   3. Lineas de 3 fichas propias no bloqueadas (triples)
     *   4. Amenazas dobles (fork: dos lineas ganadoras simultáneas)
     *
     * Cada rasgo se pondera por su peso correspondiente.
     * Los rasgos del oponente se restan con los mismos pesos.
     */

    private PesosEvaluacion pesos;

    public EvaluadorPonderado() {
        this.pesos = new PesosEvaluacion(1.0, 1.0, 1.0, 1.0);
    }

    public EvaluadorPonderado(PesosEvaluacion pesos) {
        this.pesos = pesos;
    }

    public int valoracion(Tablero tablero, int jugador) {
        int oponente = Jugador.alternarJugador(jugador);
        double funcionEvaluacion = 0;

        // Rasgo 1: Lineas de 2 fichas (propias - rivales)
        funcionEvaluacion += tablero.contarLineas(jugador,  2) * pesos.pesoLineas2;
        funcionEvaluacion -= tablero.contarLineas(oponente, 2) * pesos.pesoLineas2;

        // Rasgo 2: Control del centro (propias - rivales)
        funcionEvaluacion += tablero.contarCentro(jugador)  * pesos.pesoCentro;
        funcionEvaluacion -= tablero.contarCentro(oponente) * pesos.pesoCentro;

        // Rasgo 3: Triples (propias - rivales)
        funcionEvaluacion += tablero.contarTriples(jugador)  * pesos.pesoTriples;
        funcionEvaluacion -= tablero.contarTriples(oponente) * pesos.pesoTriples;

        // Rasgo 4: Amenazas dobles / fork (propias - rivales)
        funcionEvaluacion += tablero.amenazasDobles(jugador)  * pesos.pesoAmenazasDobles;
        funcionEvaluacion -= tablero.amenazasDobles(oponente) * pesos.pesoAmenazasDobles;

        return (int) funcionEvaluacion;
    }
}
