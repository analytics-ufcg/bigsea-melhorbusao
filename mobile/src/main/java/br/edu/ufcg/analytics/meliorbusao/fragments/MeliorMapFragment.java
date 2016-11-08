package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.MeliorBusaoApplication;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.Stop;
import br.edu.ufcg.analytics.meliorbusao.utils.DisplayMetricUtils;
import br.edu.ufcg.analytics.meliorbusao.Constants;

public abstract class MeliorMapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks {
    private LocationCallback locationCalback;
    protected Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;


    Location targetLocation = new Location("");//provider name is unecessary

    private BitmapDescriptor mParadaBitmap;

    private static String TAG = "MELIOR_MAP_FRAGMENT";

    @Override
    public void getMapAsync(OnMapReadyCallback callback) {
        super.getMapAsync(callback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = ((MeliorBusaoApplication) getActivity().getApplication()).getGoogleDetectionApiClientInstance();
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();

        initializeMap();
    }

    @Override
    public void onStop() {
        super.onStop();

        stopRequestLocationUpdates();
    }

    private void initializeMap() {
        getMap().setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (lastLocation == null) {
            requestLocationUpdates();
        } else {
            onMeliorLocationAvaliable(lastLocation);
        }
    }

    protected CameraUpdate getCameraUpdate() {
        if (mLastLocation == null){
            return CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Constants.CENTRAL_POINT_LATITUDE), Double.parseDouble(Constants.CENTRAL_POINT_LONGITUDE)), 13f);
        }
        else {
            return CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15.5f);
        }
    }

    @Override
    public void onConnectionSuspended(int i) { }


    protected void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(Constants.LOCATION_REQUEST_INTERVAL)
                .setFastestInterval(Constants.DETECTION_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, getLocationCallback(), null);
    }

    private void stopRequestLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationCallback());
    }

    private LocationCallback getLocationCallback() {
        if (locationCalback == null) {
            locationCalback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    super.onLocationResult(result);
                    onMeliorLocationAvaliable(result.getLastLocation());
                    stopRequestLocationUpdates();
                }
                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                    if (!locationAvailability.isLocationAvailable()) {

                        targetLocation.setLatitude(Double.parseDouble(Constants.CENTRAL_POINT_LATITUDE));//your coords of course
                        targetLocation.setLongitude(Double.parseDouble(Constants.CENTRAL_POINT_LONGITUDE));
                        onMeliorLocationAvaliable(targetLocation);
                    }
                }
            };
        }
        return locationCalback;
    }

    public void onMeliorLocationAvaliable(Location result) {
        mLastLocation = result;
    }

    protected MarkerOptions getMarkerOptionsFromStop(Stop parada, BitmapDescriptor bitmapDescriptor) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(parada.getLatitude(), parada.getLongitude()));
        options.icon(bitmapDescriptor);
        options.visible(true);
        options.title(parada.getName());
        if (parada.getRoutes().size() > 0) {
            StringBuilder snippet = new StringBuilder();
            for (Route r : parada.getRoutes()) {
                snippet.append(r.getShortName() + ";" + r.getColor());
                snippet.append(",");
            }
            snippet.setLength(snippet.length() - 1);
            options.snippet(snippet.toString());
        }

        return options;
    }

    protected BitmapDescriptor getBitmapDescriptor(int id, float height, float width) {
        Drawable vectorDrawable = ContextCompat.getDrawable(getContext(), id);
        int h = ((int) DisplayMetricUtils.dp2px(getResources(), height));
        int w = ((int) DisplayMetricUtils.dp2px(getResources(), width));
        vectorDrawable.setBounds(0, 0, w, h);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

}
