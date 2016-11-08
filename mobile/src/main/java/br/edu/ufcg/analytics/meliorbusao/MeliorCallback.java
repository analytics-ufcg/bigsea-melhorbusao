package br.edu.ufcg.analytics.meliorbusao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;
import java.util.Calendar;

import br.edu.ufcg.analytics.meliorbusao.listeners.MeliorListener;

public class MeliorCallback extends LocationCallback {
    private ArrayList<MeliorListener> listeners;
    private Context mContext;
    private ActivityDetectionBroadcastReceiver mReceiver;

    public MeliorCallback(Context context) {
        listeners = new ArrayList<>();
        mContext = context;
        mReceiver = new ActivityDetectionBroadcastReceiver();
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
    }

    @Override
    public void onLocationResult(LocationResult result) {
        super.onLocationResult(result);

        long timestamp = Calendar.getInstance().getTimeInMillis();
        for (MeliorListener meliorListener : listeners) {
            meliorListener.onReceiveMeliorLocation(timestamp, result.getLastLocation());
        }
    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);

        long timestamp = Calendar.getInstance().getTimeInMillis();
        for (MeliorListener meliorListener : listeners) {
            meliorListener.onMeliorLocationAvailabilityChange(timestamp, locationAvailability.isLocationAvailable());
        }
    }

    public boolean addMeliorListener(MeliorListener listener) {
        return listeners.add(listener);
    }

    public boolean removeMeliorListener(MeliorListener listener) {
        return listeners.remove(listener);
    }

    private class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);

            long timestamp = Calendar.getInstance().getTimeInMillis();
            for (MeliorListener meliorListener : listeners) {
                meliorListener.onReceiveMeliorActivity(timestamp, updatedActivities);
            }
        }
    }
}
