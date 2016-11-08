package br.edu.ufcg.analytics.meliorbusao.services;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

/**
 * IntentService for handling incoming intents that are generated as a result of requesting
 * activity updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "activity-detection-intent-service";
    protected static final String LOG_TAG = "activity-detection-is";


    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     *
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Exception e) {
            Log.e("location mode", e.getMessage());
        }
        if (SharedPreferencesUtils.isBusMonitoring(getApplicationContext()) && (locationMode != Settings.Secure.LOCATION_MODE_OFF)) {

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

            // Get the list of the probable activities associated with the current state of the
            // device. Each activity is associated with a confidence level, which is an int between
            // 0 and 100.
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            if (Constants.LOG_EACH_ACTIVITY) {
                // Log each activity.
                Log.i(LOG_TAG, "activities detected");
                for (DetectedActivity da : detectedActivities) {
                    Log.i(LOG_TAG, Constants.getActivityString(
                                    getApplicationContext(),
                                    da.getType()) + " " + da.getConfidence() + "%"
                    );

                }
            }


            // Broadcast the list of detected activities.
            localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
    }
}