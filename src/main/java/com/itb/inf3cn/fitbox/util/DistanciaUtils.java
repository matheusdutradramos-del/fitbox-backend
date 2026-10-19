
package com.itb.inf3cn.fitbox.util;

public final class DistanciaUtils {

    private DistanciaUtils() {
        // Classe utilitária: não deve ser instanciada.
    }

    /*
     * LOCALIZAÇÃO FIXA DA LOJA FICTÍCIA
     *
     * Coordenadas que já estavam no projeto:
     * Barueri - SP
     *
     * Latitude:  -23.5107
     * Longitude: -46.8763
     *
     * Para mudar a localização da loja no futuro,
     * altere somente estas duas constantes.
     */
    public static final double LATITUDE_LOJA = -23.5107;
    public static final double LONGITUDE_LOJA = -46.8763;

    /*
     * RAIO MÁXIMO DE ENTREGA
     *
     * Distância em linha reta, calculada pela fórmula
     * de Haversine.
     */
    public static final double RAIO_MAXIMO_KM = 15.0;

    private static final double RAIO_TERRA_KM = 6371.0088;

    /**
     * Verifica se uma latitude está dentro do intervalo válido.
     */
    public static boolean latitudeValida(double latitude) {
        return Double.isFinite(latitude)
                && latitude >= -90.0
                && latitude <= 90.0;
    }

    /**
     * Verifica se uma longitude está dentro do intervalo válido.
     */
    public static boolean longitudeValida(double longitude) {
        return Double.isFinite(longitude)
                && longitude >= -180.0
                && longitude <= 180.0;
    }

    /**
     * Valida um par de coordenadas geográficas.
     */
    public static boolean coordenadasValidas(
            double latitude,
            double longitude) {

        if (!latitudeValida(latitude)
                || !longitudeValida(longitude)) {
            return false;
        }

        // Evita aceitar (0, 0) como localização cadastrada.
        return latitude != 0.0 || longitude != 0.0;
    }

    /**
     * Calcula a distância em quilômetros entre dois pontos.
     *
     * A ordem dos parâmetros é sempre:
     * latitude de origem, longitude de origem,
     * latitude de destino, longitude de destino.
     */
    public static double calcularDistanciaKm(
            double latitudeOrigem,
            double longitudeOrigem,
            double latitudeDestino,
            double longitudeDestino) {

        if (!coordenadasValidas(
                latitudeOrigem, longitudeOrigem)) {
            throw new IllegalArgumentException(
                    "As coordenadas de origem são inválidas.");
        }

        if (!coordenadasValidas(
                latitudeDestino, longitudeDestino)) {
            throw new IllegalArgumentException(
                    "As coordenadas de destino são inválidas.");
        }

        double lat1 = Math.toRadians(latitudeOrigem);
        double lat2 = Math.toRadians(latitudeDestino);

        double diferencaLatitude =
                Math.toRadians(latitudeDestino - latitudeOrigem);

        double diferencaLongitude =
                Math.toRadians(longitudeDestino - longitudeOrigem);

        double senoLatitude =
                Math.sin(diferencaLatitude / 2.0);

        double senoLongitude =
                Math.sin(diferencaLongitude / 2.0);

        double a =
                senoLatitude * senoLatitude
                        + Math.cos(lat1)
                        * Math.cos(lat2)
                        * senoLongitude
                        * senoLongitude;

        // Evita erros de arredondamento fora do intervalo [0, 1].
        a = Math.max(0.0, Math.min(1.0, a));

        double c = 2.0 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1.0 - a)
        );

        return RAIO_TERRA_KM * c;
    }

    /**
     * Calcula a distância entre a loja fictícia e o cliente.
     */
    public static double calcularDistanciaDaLojaKm(
            double latitudeCliente,
            double longitudeCliente) {

        return calcularDistanciaKm(
                LATITUDE_LOJA,
                LONGITUDE_LOJA,
                latitudeCliente,
                longitudeCliente
        );
    }

    /**
     * Retorna true quando o cliente está a até 15 km
     * da localização fixa da loja.
     */
    public static boolean dentroDoRaioDeEntrega(
            double latitudeCliente,
            double longitudeCliente) {

        if (!coordenadasValidas(
                latitudeCliente, longitudeCliente)) {
            return false;
        }

        double distancia = calcularDistanciaDaLojaKm(
                latitudeCliente,
                longitudeCliente
        );

        return distancia <= RAIO_MAXIMO_KM;
    }
}
