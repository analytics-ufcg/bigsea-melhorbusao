package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnMapInformationReadyListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnMeliorBusaoQueryListener;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.utils.ProgressUtils;


public class NearStopsFragment extends Fragment implements OnMeliorBusaoQueryListener,
        SearchView.OnQueryTextListener, OnMapInformationReadyListener {

    private static NearStopsFragment instance;

    private Menu mMenu;
    private SearchView mSearchView;
    private MapFragment mMapFragment;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMapFragment = new MapFragment();
        mMapFragment.setOnMapInformationReadyListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_near_stops, container, false);
        getChildFragmentManager().beginTransaction().replace(R.id.near_stops_map_fragment, mMapFragment).commit();

        progressSpinner = ProgressUtils.buildProgressBar(getContext());

        setHasOptionsMenu(true);

        return mView;
    }


    /**
     * Add a marker in the map to each bus stop that is located within a certain diameter.
     * @param centerPoint The center of the diameter.
     */
    private void loadNearStops(GeoPoint centerPoint) {
        TreeSet<NearStop> paradas = DBUtils.getNearStops(getContext(), centerPoint.getLatitude(),
                centerPoint.getLongitude(), raio, null);
        for (NearStop parada : paradas) {
            Marker stopMarker = mMapFragment.addMarker(new GeoPoint(parada.getLatitude(), parada.getLongitude()));
            stopMarker.setIcon(getResources().getDrawable(R.drawable.ic_bus_stop_sign));
        }
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

    @Override
    public void onMapAddressFetched(String mapAddres) {

    }

    @Override
    public void onMapLocationAvailable(Location mapLocation) {
        loadNearStops(new GeoPoint(mapLocation.getLatitude(), mapLocation.getLongitude()));
    }

    @Override
    public void onMapClick(GeoPoint geoPoint) {
        mMapFragment.clearMap();
        mMapFragment.updatePlaceMarker(geoPoint);
        loadNearStops(geoPoint);
    }

    public interface OnNearStopsSelectedListener extends FragmentTitleChangeListener {
        void onClickStopWindowInfo(HashSet<Route> routes, String stopName);
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
        if (!((MelhorBusaoActivity) getActivity()).checkInternetConnection()) {
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

}
