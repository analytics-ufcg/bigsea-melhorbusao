package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnMapInformationReadyListener;
import br.edu.ufcg.analytics.meliorbusao.services.FetchAddressService;

/**
 * Represents a map and provide some features using Open Street Maps
 */
public class MapFragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MAP_ZOOM_LEVEL = 16;

    private MapView mOpenStreetMap;
    private MapController mMapController;
    private LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mPlaceMarker;
    private Marker myLocationMarker;
    private AddressResultReceiver mResultReceiver;
    private OnMapInformationReadyListener mMapListener;
    private boolean isEnabledFetchAddressService = false;
    private ImageButton myLocationButton;
    private Context mContext;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_map, container, false);

        mOpenStreetMap = (MapView) mainView.findViewById(R.id.map_view);
        mOpenStreetMap.setTileSource(TileSourceFactory.MAPNIK);
        mOpenStreetMap.setMultiTouchControls(true);
        mOpenStreetMap.getOverlays().add(new MapOverlay(getContext()));

        mMapController = (MapController) mOpenStreetMap.getController();
        mMapController.setZoom(MAP_ZOOM_LEVEL);

        myLocationButton = (ImageButton) mainView.findViewById(R.id.my_location_button);
        setUpMyLocationButton();

        initializePlaceMarker();
        initializeMyLocationMarker();
        buildGoogleApiClient();

        return mainView;
    }

    private void setUpMyLocationButton() {
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastLocation != null) {
                    GeoPoint myLocationGeopoint = new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mMapController.animateTo(myLocationGeopoint);
                }
            }
        });
    }

    /**
     * Initialize the place marker and its listeners
     */
    private void initializePlaceMarker() {
        mPlaceMarker = new Marker(mOpenStreetMap);
        mPlaceMarker.setIcon(getResources().getDrawable(R.drawable.ic_map_marker));
        mPlaceMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mPlaceMarker.setDraggable(true);

        mPlaceMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                return false;
            }
        });
        mPlaceMarker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });
    }

    /**
     * Initialize my location marker
     */
    private void initializeMyLocationMarker() {
        myLocationMarker = new Marker(mOpenStreetMap);
        myLocationMarker.setIcon(getResources().getDrawable(R.drawable.ic_my_location_marker));
        myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        myLocationMarker.setDraggable(false);
        myLocationMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                return false;
            }
        });
    }

    /**
     * Call connection for mGoogleApiClient
     */
    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * Updates the place marker by removing it from the map overlays, updating its position and then adding it again.
     *
     * @param markerPosition The new position where the marker should be.
     */
    public void updatePlaceMarker(GeoPoint markerPosition) {
        mOpenStreetMap.getOverlays().remove(mPlaceMarker);

        mPlaceMarker.setPosition(markerPosition);

        mOpenStreetMap.getOverlays().add(mPlaceMarker);
        mOpenStreetMap.invalidate();
        mMapController.animateTo(markerPosition);
        if (isEnabledFetchAddressService) {
            startIntentService(markerPosition);
        }
    }

    /**
     * Updates my location marker;
     *
     * @param myLocationPosition The new position where the marker should be.
     */
    private void updateMyLocationMarker(GeoPoint myLocationPosition) {
        mOpenStreetMap.getOverlays().remove(myLocationMarker);
        myLocationMarker.setPosition(myLocationPosition);
        mOpenStreetMap.getOverlays().add(myLocationMarker);
        mOpenStreetMap.invalidate();
    }

    /**
     * Add an Marker object to the map.
     *
     * @param markerPosition The position of the marker in the map.
     * @return The marker object that was added to the map.
     */
    public Marker addMarker(GeoPoint markerPosition) {
        Marker marker = new Marker(mOpenStreetMap);
        marker.setPosition(markerPosition);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mOpenStreetMap.getOverlays().add(marker);
        mOpenStreetMap.invalidate();
        return marker;
    }

    /**
     * Enable the Fetch Address Service.
     */
    public void enableFetchAddressService() {
        this.isEnabledFetchAddressService = true;
    }

    /**
     * Clear all the markers in the map except for the my location marker.
     */
    public void clearMap() {
        mOpenStreetMap.getOverlays().clear();
        mOpenStreetMap.getOverlays().add(new MapOverlay(getContext()));
        if (mLastLocation != null) {
            updateMyLocationMarker(new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            if (isEnabledFetchAddressService) {
                GeoPoint geoPoint = new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                startIntentService(geoPoint);
            }
        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        GeoPoint newCenter = new GeoPoint(location.getLatitude(), location.getLongitude());
        updateMyLocationMarker(newCenter);
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
        if (mLastLocation != null) {
            GeoPoint actualLocation = new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            updateMyLocationMarker(actualLocation);
            mMapController.animateTo(actualLocation);
            if (mMapListener != null) {
                mMapListener.onMapLocationAvailable(mLastLocation);
            }
        }
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
        return new GeoPoint(centerPoint.getLatitude(), centerPoint.getLongitude());
    }

    /**
     * Starts a service that fetchs the address corresponding to a geopoint's latitude and longitude.
     *
     * @param geoPoint The point that holds the coordinates for searching the address.
     */
    protected void startIntentService(GeoPoint geoPoint) {
        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(getContext(), FetchAddressService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.GEOPOINT_DATA_EXTRA, (Parcelable) geoPoint);
        getActivity().startService(intent);
    }

    /**
     * Set the OnMapInformationReadyListener for this map.
     *
     * @param mapListener the listener that must be set.
     */
    public void setOnMapInformationReadyListener(OnMapInformationReadyListener mapListener) {
        this.mMapListener = mapListener;
    }

    /**
     * Handles the results found by the FetchAddressService
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (mMapListener != null) {
                mMapListener.onMapAddressFetched(addressOutput);
            }
        }
    }

    /**
     * A map overlay that listens to taps on the map.
     */
    class MapOverlay extends Overlay {

        public MapOverlay(Context ctx) {
            super(ctx);
        }

        @Override
        protected void draw(Canvas canvas, MapView mapView, boolean b) {

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
            InfoWindow.closeAllInfoWindowsOn(mOpenStreetMap);
            Projection mProjection = mOpenStreetMap.getProjection();
            GeoPoint geoPoint = (GeoPoint) mProjection.fromPixels((int) e.getX(), (int) e.getY());
            updatePlaceMarker(geoPoint);
            if (mMapListener != null) {
                mMapListener.onMapClick(geoPoint);
            }
            return super.onSingleTapConfirmed(e, mapView);
        }
    }

    /**
     * Returns the map view.
     */
    public MapView getMapView() {
        return mOpenStreetMap;
    }
}
