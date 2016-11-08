package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MeliorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.adapters.StopInfoAdapter;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnMeliorBusaoQueryListener;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.utils.ProgressUtils;


import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;


public class NearStopsFragment extends MeliorMapFragment implements
        GoogleMap.OnInfoWindowClickListener, OnMeliorBusaoQueryListener,
        SearchView.OnQueryTextListener, GoogleMap.OnMapClickListener {

    private static NearStopsFragment instance;

    private OnNearStopsSelectedListener mCallback;
    private FloatingActionButton mReloadButton;
    private Menu mMenu;
    private SearchView mSearchView;

    private final double raio = 500;

    private String address = "";

    public static final String TAG = "NEAR_STOPS_FRAGMENT";
    private BitmapDescriptor mParadaBitmap;
    private ProgressBar progressSpinner;


    public static NearStopsFragment getInstance() {
        if (instance == null) {
            NearStopsFragment fragment = new NearStopsFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            instance = fragment;
        }
        return instance;
    }

    public NearStopsFragment() {
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String[] routes = marker.getSnippet().split(",");
        //ArrayList<SumarioRota> listSummaryRoutesThisMarker = new ArrayList<SumarioRota>();
        HashSet<Route> hashRoute = new HashSet<Route>();

        for (String route : routes) {
            String[] routeAux = route.split(";");
            //SumarioRota summaryRoute = DBUtils.getSumarioRota(getContext(), DBUtils.getRoute(getContext(), routeAux[0]));
            hashRoute.add(DBUtils.getRoute(getContext(), routeAux[0]));
            //listSummaryRoutesThisMarker.add(summaryRoute);
        }

        String stopName = marker.getTitle().substring(marker.getTitle().indexOf(" - ") + 3);
        if (stopName.substring(0, 1).matches("[0-9]")) {
            stopName = getString(R.string.bus_stop) + stopName;
        }
        mCallback.onClickStopWindowInfo(hashRoute, stopName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout viewMain = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);

        progressSpinner = ProgressUtils.buildProgressBar(getContext());

        getMap().setOnMapClickListener(this);
        getMap().setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                if (((MeliorBusaoActivity) getActivity()).isLocationEnabled()) {
                    requestLocationUpdates();
                    Snackbar.make(getView(), R.string.reload_location, Snackbar.LENGTH_LONG).show();
                } else {
                    ((MeliorBusaoActivity) getActivity()).buildAlertMessageNoGps();
                }
                return true;
            }
        });
//        getMap().setOnMapLongClickListener(this);

        //Enable Options Menu handling
        setHasOptionsMenu(true);

        return viewMain;
    }

    private void setUpMap() {
        getMap().clear();
        getMap().setMyLocationEnabled(true);
        getMap().animateCamera(getCameraUpdate());
        getMap().addCircle(getCircleOptions());
        inicializarParadas();
        StopInfoAdapter stopInfo = new StopInfoAdapter();
        stopInfo.setActivity(getActivity());
        getMap().setInfoWindowAdapter(stopInfo);
        getMap().setOnInfoWindowClickListener(this);

        progressSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onMeliorLocationAvaliable(Location result) {
        super.onMeliorLocationAvaliable(result);

        setUpMap();
    }

    private CameraUpdate getCameraUpdate(LatLng point) {
        return CameraUpdateFactory.newLatLngZoom(new LatLng(point.latitude, point.longitude), 15.5f);
    }

    private CircleOptions getCircleOptions() {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.radius(raio);
        circleOptions.fillColor(ContextCompat.getColor(getContext(), R.color.nearStopsAreaFill));
        circleOptions.strokeColor(ContextCompat.getColor(getContext(), R.color.nearStopsAreaStroke));
        circleOptions.strokeWidth(3);
        circleOptions.center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        return circleOptions;
    }

    private void inicializarParadas() {
        TreeSet<NearStop> paradas = DBUtils.getNearStops(getContext(), mLastLocation.getLatitude(),
                mLastLocation.getLongitude(), raio, null);
        for (NearStop parada : paradas) {
            getMap().addMarker(getMarkerOptionsFromStop(parada, getStopBitmap()));
        }
    }

    private void inicializarParadas(LatLng point) {
        TreeSet<NearStop> paradas = DBUtils.getNearStops(getContext(), point.latitude,
                point.longitude, raio, null);
        for (NearStop parada : paradas) {
            getMap().addMarker(getMarkerOptionsFromStop(parada, getStopBitmap()));
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        progressSpinner.setVisibility(View.VISIBLE);
        setAddressName(latLng.latitude, latLng.longitude);
        updateNearStops(latLng);
    }

    private void setAddressName(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            setAddress(addresses.get(0).getAddressLine(0));

        } catch (Exception e) {
            Log.e("Erro address", e.getMessage());
        }
    }

    private void updateNearStops(LatLng point) {
        getMap().clear();
        getMap().animateCamera(getCameraUpdate(point));
        getMap().addCircle(getCircleOptions(point));

        getMap().addMarker(new MarkerOptions().position(point).title(
                getAddress()));
        inicializarParadas(point);
        progressSpinner.setVisibility(View.GONE);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private CircleOptions getCircleOptions(LatLng point) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.radius(raio);
        circleOptions.fillColor(ContextCompat.getColor(getContext(), R.color.nearStopsAreaFill));
        circleOptions.strokeColor(ContextCompat.getColor(getContext(), R.color.nearStopsAreaStroke));
        circleOptions.strokeWidth(3);
        circleOptions.center(new LatLng(point.latitude, point.longitude));
        return circleOptions;
    }

    @Override
    public void onMeliorBusaoQueryChange(String query) {

    }

    @Override
    public boolean onMeliorBusaoQuerySubmit(String query) {
        try {
            address = query;
            try {
                Geocoder geocoder;
                geocoder = new Geocoder(getContext(), Locale.getDefault());
                LatLng point = new LatLng(geocoder.getFromLocationName(address + "," + Constants.CITY, 1).get(0).getLatitude(),
                        geocoder.getFromLocationName(address + "," + Constants.CITY, 1).get(0).getLongitude());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                if (addresses.get(0).getLocality().compareTo(Constants.CITY) == 0) {
                    setAddressName(point.latitude, point.longitude);
                    updateNearStops(point);
                    return true;
                } else {
                    Toast.makeText(getActivity(), getString(R.string.msg_unable_to_find_route), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("NearStopsFragment", e.getMessage());
            }

        } catch (Exception e) {
            Log.e("NearStopsFragment", "Não foi possível achar a localizacao: " + e.getMessage());
            Toast.makeText(getActivity(), getString(R.string.msg_failed_detect_location), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public interface OnNearStopsSelectedListener extends FragmentTitleChangeListener {
        void onClickStopWindowInfo(HashSet<Route> routes, String stopName);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNearStopsSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallback.onTitleChange(getResources().getString(R.string.near_stops_title));
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
    public boolean onQueryTextSubmit(String query) {
        if (!((MeliorBusaoActivity) getActivity()).checkInternetConnection()) {
            Toast.makeText(getContext(), getString(R.string.msg_search_needs_internet), Toast.LENGTH_LONG).show();
            return false;

        } else {
            try {
                address = query;
                try {
                    Geocoder geocoder;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                    LatLng point = new LatLng(geocoder.getFromLocationName(address + "," + Constants.CITY, 1).get(0).getLatitude(),
                            geocoder.getFromLocationName(address + "," + Constants.CITY, 1).get(0).getLongitude());
                    List<Address> addresses;
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                    if (addresses.get(0).getLocality().compareTo(Constants.CITY) == 0) {
                        updateNearStops(point);
                        mMenu.findItem(R.id.action_search).collapseActionView();
                        return true;
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_failed_locate_search), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("NearStopsFragment", e.getMessage());
                }

            } catch (Exception e) {
                Log.e("NearStopsFragment", "Não foi possível achar a localizacao: " + e.getMessage());
                Toast.makeText(getActivity(), getString(R.string.msg_failed_detect_location), Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }


    private BitmapDescriptor getStopBitmap() {
        if (mParadaBitmap == null) {
            mParadaBitmap = getBitmapDescriptor(R.drawable.ic_parada, 52, 40);
        }
        return mParadaBitmap;
    }

}
