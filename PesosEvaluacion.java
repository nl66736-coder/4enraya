public class PesosEvaluacion {

    public double pesoLineas2;       // peso para lineas de 2 fichas propias
    public double pesoCentro;        // peso para control del centro
    public double pesoTriples;       // peso para lineas de 3 fichas propias
    public double pesoAmenazasDobles;// peso para amenazas dobles (fork)

    public PesosEvaluacion() {
        this(1.0, 1.0, 1.0, 1.0);
    }

    public PesosEvaluacion(double pesoLineas2, double pesoCentro,
                           double pesoTriples, double pesoAmenazasDobles) {
        this.pesoLineas2        = pesoLineas2;
        this.pesoCentro         = pesoCentro;
        this.pesoTriples        = pesoTriples;
        this.pesoAmenazasDobles = pesoAmenazasDobles;
    }

    /** Devuelve una copia independiente de estos pesos */
    public PesosEvaluacion copia() {
        return new PesosEvaluacion(pesoLineas2, pesoCentro, pesoTriples, pesoAmenazasDobles);
    }

    @Override
    public String toString() {
        return String.format("PesosEvaluacion[lineas2=%.3f, centro=%.3f, triples=%.3f, amenazasDobles=%.3f]",
                pesoLineas2, pesoCentro, pesoTriples, pesoAmenazasDobles);
    }
}