package br.edu.ufcg.analytics.meliorbusao.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import br.edu.ufcg.analytics.meliorbusao.services.LocationService;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "BootCompletedReceiver";

    public BootCompletedReceiver() { }

    /**
     * Verifica se o serviço de localização está no ar
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName componentName = context.startService(
                new Intent(context, LocationService.class));

        if (componentName != null) {
            Log.d(LOG_TAG, "LocationService is up and running...");
        }
    }


}
