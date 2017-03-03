package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.adapters.InfoWindowAdapter;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnMapInformationReadyListener;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Route;


public class NearStopsFragment extends Fragment implements SearchView.OnQueryTextListener,
        OnMapInformationReadyListener {

    public static final String TAG = "NearStopsFragment";
    private static final double RAIO = 500;
    private static NearStopsFragment instance;
    private MapFragment mMapFragment;
    private Menu mMenu;
    private NearStopListener mCallback;
    private SearchView mSearchView;

    public NearStopsFragment() {
    }

    public static NearStopsFragment getInstance() {
        if (instance == null) {
            NearStopsFragment fragment = new NearStopsFragment();
            instance = fragment;
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapFragment = new MapFragment();
        mMapFragment.setOnMapInformationReadyListener(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_near_stops, container, false);
        getChildFragmentManager().beginTransaction().replace(R.id.near_stops_map_fragment, mMapFragment).commit();
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;

        menu.findItem(R.id.list_routes_btn).setVisible(false);
        menu.findItem(R.id.expand_button).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(true);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint_near_stops));
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MelhorBusaoActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.near_stops_title);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (NearStopListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NearStopListener");
        }
    }

    /**
     * Add a marker in the map to each bus stop that is located within a certain diameter.
     * @param centerPoint The center of the diameter.
     */
    private void loadNearStops(GeoPoint centerPoint) {
        TreeSet<NearStop> nearStops = DBUtils.getNearStops(getContext(), centerPoint.getLatitude(),
                centerPoint.getLongitude(), RAIO, null);
        for (NearStop stop : nearStops) {
            addNearStopMarker(stop);
        }
    }

    /**
     * Add a marker for representing a near stop in the map.
     * @param stop The Stop object to be represented by the marker.
     */
    private void addNearStopMarker(NearStop stop) {
        Marker stopMarker = mMapFragment.addMarker(new GeoPoint(stop.getLatitude(), stop.getLongitude()));
        stopMarker.setIcon(getResources().getDrawable(R.drawable.ic_bus_stop_sign));
        stopMarker.setInfoWindow(new NearStopMarkerInfoWindow(mMapFragment.getMapView(), stop));
        stopMarker.setInfoWindowAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
        stopMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                marker.showInfoWindow();
                return true;
            }
        });
    }


    @Override
    public void onMapAddressFetched(String mapAddres) {
    }

    @Override
    public void onMapLocationAvailable(Location mapLocation) {
        loadNearStops(new GeoPoint(mapLocation.getLatitude(), mapLocation.getLongitude()));
    }

    /**
     * Update the nearby stops when the map is tapped.
     * @param geoPoint The Geopoint that represents the point where the map was tapped.
     */
    @Override
    public void onMapClick(GeoPoint geoPoint) {
        mMapFragment.clearMap();
        mMapFragment.updatePlaceMarker(geoPoint);
        loadNearStops(geoPoint);
    }

    /**
     * Mark a new place in the map according to the addres passed if the address exists.
     *
     * @param query The addres for looking up.
     * @return If the address was successfully found or not.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!((MelhorBusaoActivity) getActivity()).checkInternetConnection()) {
            Toast.makeText(getContext(), getString(R.string.msg_search_needs_internet), Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                Geocoder geocoder;
                geocoder = new Geocoder(getContext(), Locale.getDefault());
                LatLng point = new LatLng(geocoder.getFromLocationName(query + "," + Constants.CITY, 1).get(0).getLatitude(),
                        geocoder.getFromLocationName(query + "," + Constants.CITY, 1).get(0).getLongitude());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                if (addresses.get(0).getLocality().compareTo(Constants.CITY) == 0) {
                    GeoPoint geoPoint = new GeoPoint(point.latitude, point.longitude);
                    mMapFragment.clearMap();
                    mMapFragment.updatePlaceMarker(geoPoint);
                    loadNearStops(geoPoint);
                    mMenu.findItem(R.id.action_search).collapseActionView();
                    return true;
                } else {
                    Toast.makeText(getActivity(), getString(R.string.msg_failed_locate_search), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    /**
     * Listener for NearStopFragment
     */
    public interface NearStopListener extends FragmentTitleChangeListener {
        void onInfoWindowClick(HashSet<Route> routes, String stopName);
    }

    /**
     * Class representing the info window that shows up when clicking in a marker.
     */
    class NearStopMarkerInfoWindow extends MarkerInfoWindow {

        private NearStop mStop;
        private MapView mMapView;

        public NearStopMarkerInfoWindow(MapView mapView, NearStop stop) {
            super(R.layout.near_stop_info_window, mapView);
            this.mStop = stop;
            this.mMapView = mapView;
        }

        @Override
        public void onOpen(Object item) {
            closeAllInfoWindowsOn(mMapView);
            GridView infoWindowGrid = (GridView) mView.findViewById(R.id.info_window_grid);
            infoWindowGrid.setAdapter(new InfoWindowAdapter(mView.getContext(), mStop.getRoutes()));
            TextView windowTitle = (TextView) mView.findViewById(R.id.bubble_title);
            windowTitle.setText(mStop.getName());
// // TODO: 15/02/17 continuar isso aqui 
//            Rect rectf = new Rect();
//            mView.requestaRectangleOnScreen(rectf);
//            MapController mapController = (MapController) mMapView.getController();
//            int x = rectf.right + rectf.width();
//            int y = rectf.top - (rectf.height() / 2);
//            mapController.animateTo(x, y);

            infoWindowGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mCallback != null) {
                        mCallback.onInfoWindowClick(new HashSet<>(mStop.getRoutes()), mStop.getName());
                    }
                }
            });
        }

    }

}