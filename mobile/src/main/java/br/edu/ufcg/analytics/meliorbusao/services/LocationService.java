package br.edu.ufcg.analytics.meliorbusao.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import br.edu.ufcg.analytics.meliorbusao.detection.BusDetector;
import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.detection.LocationRecorder;
import br.edu.ufcg.analytics.meliorbusao.MeliorBusaoApplication;
import br.edu.ufcg.analytics.meliorbusao.MeliorCallback;
import br.edu.ufcg.analytics.meliorbusao.NotificationTrigger;

public class LocationService extends Service implements ResultCallback<Status>, GoogleApiClient.ConnectionCallbacks {
    protected static final String TAG = "location-recorder-intent-service";
    protected static final String LOG_TAG = "location-recorder";

    private PowerManager.WakeLock wakelock;
    private LocationRecorder locationRecorder;
    private boolean recording;
    private boolean startRecordingWaitingForConnection;

    private MeliorCallback mMeliorCallback;
    private BusDetector mBusDetector;
    private NotificationTrigger mNotificationTrigger;

    private OnBusBroadcastReceiver activityDetectionBroadcastReceiver;
    private GoogleApiClient mGoogleApiClient;

    private PendingIntent mActivityDetectionPendingIntent;

    private long startTime;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startRecordingWaitingForConnection = false;

        mMeliorCallback = new MeliorCallback(this);

        mGoogleApiClient = ((MeliorBusaoApplication) getApplication()).getGoogleDetectionApiClientInstance();
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.connect();

        mBusDetector = new BusDetector(this);
        mNotificationTrigger = new NotificationTrigger(this);

        mMeliorCallback.addMeliorListener(mBusDetector);
        mMeliorCallback.addMeliorListener(mNotificationTrigger);

        activityDetectionBroadcastReceiver = new OnBusBroadcastReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(activityDetectionBroadcastReceiver, new IntentFilter(Constants.BROADCAST_ACTION_ON_BUS));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startRecording(long startTime) {
        if (recording)
            return;
        if (!mGoogleApiClient.isConnected()) {
            requestConnectionForStartRecording();
            return;
        }

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.LOCATION_RECORDER_WAKELOCK);
        wakelock.acquire();

        locationRecorder = new LocationRecorder(getApplicationContext(), String.valueOf(startTime));
        mMeliorCallback.addMeliorListener(locationRecorder);

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(Constants.LOCATION_REQUEST_INTERVAL)
                .setFastestInterval(Constants.DETECTION_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, mMeliorCallback, null);

        locationRecorder.startRecording(startTime);

        recording = true;
    }

    private void requestConnectionForStartRecording() {
        startRecordingWaitingForConnection = true;
        mGoogleApiClient.connect();
    }

    private void stopRecording() {
        if (!recording)
            return;

        locationRecorder.stopRecording();
        mMeliorCallback.removeMeliorListener(locationRecorder);

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mMeliorCallback);
        if (wakelock != null && wakelock.isHeld()) {
            wakelock.release();
        }

        recording = false;
    }

    public void requestActivityUpdates() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mActivityDetectionPendingIntent != null) {
            return mActivityDetectionPendingIntent;
        }
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return (mActivityDetectionPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    public void onResult(Status status) {
        if (!status.isSuccess()) {
            Log.e(LOG_TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "O Service está de pé e sabe que a API conectou");
        requestActivityUpdates();
        if (startRecordingWaitingForConnection) {
            startRecording(startTime);
            startRecordingWaitingForConnection = false;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public class OnBusBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean onBus = intent.getExtras().getBoolean(Constants.ON_BUS_STATUS_EXTRA);
            startTime = intent.getExtras().getLong(Constants.ON_BUS_TIMESTAMP_EXTRA);
            Log.d("OnBusBradcastReceiver", String.valueOf(onBus));

            if (onBus) {
                LocationService.this.startRecording(startTime);
            } else {
                LocationService.this.stopRecording();
            }
        }
    }
}
