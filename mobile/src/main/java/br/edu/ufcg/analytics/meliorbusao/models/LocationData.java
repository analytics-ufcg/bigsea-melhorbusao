package br.edu.ufcg.analytics.meliorbusao.models;

/**
 * Interface que provê dados de geolocalização
 */
public interface LocationData extends ILatLng {
    float getAccuracy();
    float getSpeed();
    float getBearing();
    long getTime();
}
