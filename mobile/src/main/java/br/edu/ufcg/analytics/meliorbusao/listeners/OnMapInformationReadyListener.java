package br.edu.ufcg.analytics.meliorbusao.listeners;

import android.location.Location;

public interface OnMapInformationReadyListener {

    void onMapAddressFetched(String mapAddres);

    void onMapLocationAvailable(Location mapLocation);
}
