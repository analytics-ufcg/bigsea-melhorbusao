package br.edu.ufcg.analytics.meliorbusao.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

public class LocationTracker {


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    protected LocationTracker() {}

    /**
     * De tempos em tempos são capturadas a latitude, longitude e outros atributos para salvar no bd
     * @param currActivity
     * @param locationListener
     * @return
     */
    public static Location getLastLocation(Activity currActivity, LocationListener locationListener) {
        Location mLocation = null;
        String provider;
        try {
            LocationManager mlocManager = (LocationManager) currActivity.
                    getSystemService(Context.LOCATION_SERVICE);

            provider = mlocManager.isProviderEnabled(GPS_PROVIDER)? GPS_PROVIDER :
                    mlocManager.isProviderEnabled(NETWORK_PROVIDER)? NETWORK_PROVIDER : null;

            mlocManager.requestLocationUpdates(
                    provider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) locationListener);
            mLocation = mlocManager
                    .getLastKnownLocation(provider);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLocation;
    }
}
