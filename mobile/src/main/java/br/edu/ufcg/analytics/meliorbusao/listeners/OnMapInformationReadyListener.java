package br.edu.ufcg.analytics.meliorbusao.listeners;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

public interface OnMapInformationReadyListener {

    void onMapAddressFetched(String mapAddres);

    void onMapLocationAvailable(Location mapLocation);

    void onMapClick(GeoPoint geoPoint);
}
