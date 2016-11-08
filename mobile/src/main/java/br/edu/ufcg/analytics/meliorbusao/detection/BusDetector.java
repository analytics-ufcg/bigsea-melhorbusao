package br.edu.ufcg.analytics.meliorbusao.detection;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.DetectedActivity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.listeners.MeliorListener;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

public class BusDetector implements MeliorListener {
    private LinkedList<Character> lastActivitiesBuffer;
    private Context mContext;

    private boolean onBus;

    public BusDetector(Context context) {
        mContext = context;

        lastActivitiesBuffer = new LinkedList<>(Arrays.asList('X', 'X', 'X'));
    }

    @Override
    public void onReceiveMeliorLocation(long timestamp, Location lastLocation) {}

    @Override
    public void onMeliorLocationAvailabilityChange(long timestamp, boolean locationAvailable) {}

    /**
     *
     * @param timestamp
     * @param detectedActivity
     */
    @Override
    public void onReceiveMeliorActivity(long timestamp, List<DetectedActivity> detectedActivity) {

        List<DetectedActivity> updatedActivities = detectedActivity;

        //if negative, out of city limits waiting time
        if ((System.currentTimeMillis() -
                (Long.valueOf(SharedPreferencesUtils.getDeactivateTime(mContext)) + Constants.DEACTIVATED_TIME)) < 0) {
            setOnBus(false);
            broadcastOnBusStatus(false, timestamp);
        }
        else if (updatedActivities.get(0).getType() == DetectedActivity.IN_VEHICLE && updatedActivities.get(0).getConfidence() > 70) {
            lastActivitiesBuffer.remove();
            lastActivitiesBuffer.add('V');

            if (!isOnBus()) {
                setOnBus(true);
                broadcastOnBusStatus(true, timestamp);
            }

        } else if (updatedActivities.get(0).getType() == DetectedActivity.ON_FOOT && updatedActivities.get(0).getConfidence() > 50) {
            lastActivitiesBuffer.remove();
            lastActivitiesBuffer.add('S'); //S de STOP =)
            int count = 0;
            for (int i = 0; i < lastActivitiesBuffer.size(); i++) {
                if (lastActivitiesBuffer.get(i) == 'S')
                    count += 1;
            }

            if (count == 3 && onBus) {
                setOnBus(false);
                broadcastOnBusStatus(onBus, timestamp);
            }
        } else {
            lastActivitiesBuffer.remove();
            lastActivitiesBuffer.add('U'); //U de unknown =)
        }

    }


    /**
     *
     * @param status
     * @param timestamp
     * @return
     */
    private boolean broadcastOnBusStatus(boolean status, long timestamp) {
        return LocalBroadcastManager.getInstance(mContext).sendBroadcast(createOnBusBroadcastIntent(status, timestamp));
    }

    /**
     *
     * @param status
     * @param timestamp
     * @return
     */
    private Intent createOnBusBroadcastIntent(boolean status, long timestamp) {
        Intent broadcastIntent = new Intent(Constants.BROADCAST_ACTION_ON_BUS);
        broadcastIntent.putExtra(Constants.ON_BUS_STATUS_EXTRA, status);
        broadcastIntent.putExtra(Constants.ON_BUS_TIMESTAMP_EXTRA, timestamp);

        return broadcastIntent;
    }

    /**
     * Retorna se está no ônibus ou não
     * @return
     */
    public boolean isOnBus() {
        return onBus;
    }

    /**
     * Atualiza a variavel que informa se está ou não no onibus
     */
    private void setOnBus(boolean onBus) {
        this.onBus = onBus;
    }
}
