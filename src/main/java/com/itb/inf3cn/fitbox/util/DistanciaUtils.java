package com.itb.inf3cn.fitbox.util;

public class DistanciaUtils {

    // ==========================================
    // COORDENADAS DA LOJA / ESCOLA (BARUERI - SP)
    // ==========================================

    public static final double LATITUDE_LOJA = -23.5107;
    public static final double LONGITUDE_LOJA = -46.8763;

    // Raio máximo de entrega permitido, em quilômetros
    public static final double RAIO_MAXIMO_KM = 20.0;

    // ==========================================
    // FÓRMULA DE HAVERSINE
    // Calcula a distância em km entre dois pontos
    // (latitude/longitude) na superfície da Terra
    // ==========================================

    public static double calcularDistanciaKm(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double RAIO_TERRA_KM = 6371.0;

        double deltaLat =
                Math.toRadians(lat2 - lat1);

        double deltaLon =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c =
                2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_KM * c;
    }

    // ==========================================
    // VERIFICA SE UM PONTO ESTÁ DENTRO DO RAIO
    // DE ENTREGA A PARTIR DA LOJA
    // ==========================================

    public static boolean dentroDoRaioDeEntrega(
            double latitudeCliente,
            double longitudeCliente) {

        double distancia = calcularDistanciaKm(
                LATITUDE_LOJA,
                LONGITUDE_LOJA,
                latitudeCliente,
                longitudeCliente
        );

        return distancia <= RAIO_MAXIMO_KM;
    }
}
