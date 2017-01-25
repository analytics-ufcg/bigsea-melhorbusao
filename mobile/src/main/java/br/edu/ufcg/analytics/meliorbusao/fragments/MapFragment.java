package br.edu.ufcg.analytics.meliorbusao.fragments;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import br.edu.ufcg.analytics.meliorbusao.R;


public class MapFragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MAP_ZOOM_LEVEL = 15;

    private MapView mOpenStreetMap;
    private MapController mMapController;
    private LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_map, container, false);

        mOpenStreetMap = (MapView) mainView.findViewById(R.id.map_view);
        mOpenStreetMap.setTileSource(TileSourceFactory.MAPNIK);
        mOpenStreetMap.setBuiltInZoomControls(true);
        mOpenStreetMap.setMultiTouchControls(true);

        mMapController = (MapController) mOpenStreetMap.getController();
        mMapController.setZoom(MAP_ZOOM_LEVEL);

        buildGoogleApiClient();

        return mainView;
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void addMarker(GeoPoint center) {
        Marker marker = new Marker(mOpenStreetMap);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.mipmap.map_marker));

        mOpenStreetMap.getOverlays().clear();
        mOpenStreetMap.getOverlays().add(marker);
        mOpenStreetMap.invalidate();

        mMapController.animateTo(center);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        GeoPoint newCenter = new GeoPoint(location.getLatitude(), location.getLongitude());
        mMapController.animateTo(newCenter);
        addMarker(newCenter);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        GeoPoint actualLocation = new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        addMarker(actualLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Nullable
    public GeoPoint getLocation() {
        IGeoPoint centerPoint = mOpenStreetMap.getMapCenter();
        mOpenStreetMap.getOverlays().get(0).
        return new GeoPoint(centerPoint.getLatitude(), centerPoint.getLongitude());
    }
}
