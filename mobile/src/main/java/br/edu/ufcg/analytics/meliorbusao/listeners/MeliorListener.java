package br.edu.ufcg.analytics.meliorbusao.listeners;

import android.location.Location;

import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public interface MeliorListener {
    void onReceiveMeliorLocation(long timestamp, Location lastLocation);
    void onMeliorLocationAvailabilityChange(long timestamp, boolean locationAvailable);
    void onReceiveMeliorActivity(long timestamp, List<DetectedActivity> detectedActivity);
}
