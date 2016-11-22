package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.MeliorBusaoApplication;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.adapters.RouteArrayAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.StopArrayAdapter;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnStopTimesReadyListener;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.Stop;
import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;
import br.edu.ufcg.analytics.meliorbusao.utils.ParseUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.StopRouteUtils;

public class SearchScheduleFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, View.OnClickListener, OnStopTimesReadyListener, SearchView.OnQueryTextListener {

    public static final String TAG = "SEARCH_SCHEDULE_FRAG";
    private static SearchScheduleFragment instance;
    ArrayList<StopHeadsign> paradasDisponiveis;
    private OnTakeBusSelectedListener mCallback;
    private Location mLocation;
    private View mView;
    private ArrayList<Route> rotas;
    private Route selectedRoute;
    private LocationCallback locationCallback;
    private GoogleApiClient mGoogleApiClient;
    private Route selectedRouteName;
    private Button takeBusButton;
    private Spinner stopsSpinner;
    private Spinner routesSpinner;
    private HashSet<Stop> paradasDaRota;
    private ArrayList<NearStop> paradasProximas;
    private RouteArrayAdapter mAdapter;
    private StopArrayAdapter mAdapterStop;
    private SearchView mSearchView;
    private SimpleMapFragment mapFragment;

    private TextView addressField;

    private Menu mMenu;

    public SearchScheduleFragment() {
    }

    /**
     * Returns one instance of SearchScheduleFragment (it guarantees that there is only one SearchScheduleFragment object at the same time)
     * @return
     */
    public static SearchScheduleFragment getInstance() {
        if (instance == null) {
            SearchScheduleFragment fragment = new SearchScheduleFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            instance = fragment;
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = ((MeliorBusaoApplication) getActivity().getApplication()).getGoogleDetectionApiClientInstance();
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.connect();

        mapFragment = new SimpleMapFragment();

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        String screenTitle = getResources().getString(R.string.bus_schedule_title);
        mCallback.onTitleChange(screenTitle);

        if (((MelhorBusaoActivity) getActivity()).isLocationEnabled()) {
            Log.d(TAG, "Ativou o gps");
            requestLocationUpdates();
            setRouteAdapter();
        } else {
            ((MelhorBusaoActivity) getActivity()).buildAlertMessageNoGps();
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnTakeBusSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search_schedule, container, false);

        getChildFragmentManager().beginTransaction().replace(R.id.melior_map_fragment, mapFragment).commit();

        routesSpinner = (Spinner) mView.findViewById(R.id.take_bus_routes_spinner);
        routesSpinner.setOnItemSelectedListener(this);

        stopsSpinner = (Spinner) mView.findViewById(R.id.take_bus_stops_spinner);

        //not work very well
        /*stopsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mapFragment.getMap().addMarker(new MarkerOptions().position(point).title(getAddress()).
                        icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker)));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        takeBusButton = (Button) mView.findViewById(R.id.take_bus_take_bus_button);
        takeBusButton.setEnabled(false);
        takeBusButton.setBackgroundColor(Color.parseColor("#DCDCDC"));
//        takeBusButton.setBackgroundColor(getResources().getColor(R.color.white_gray));
        takeBusButton.setOnClickListener(this);

        return mView;
    }


    /**
     * Muda o adpater de Rotas de acordo com a localização atual do usuario
     */
    protected void setRouteAdapter() {
        getNearRoutes();
        Collections.sort(rotas);

        Boolean existRoute = false;

        if (selectedRouteName != null) {

            if (rotas.contains(selectedRoute)){
                for (int i = 0; i < rotas.size(); i++) {
                    if (rotas.get(i).getShortName().equals(selectedRouteName.getShortName())) {
                        final int finalI = i;
                        routesSpinner.post(new Runnable() {
                            @Override
                            public void run() {
                                routesSpinner.setSelection(finalI);
                            }
                        });
                        break;
                    }
                }
            } else {
                Toast.makeText(getContext(), "O ônibus escolhido não passa nesta área.", Toast.LENGTH_LONG).show();
            }
        }
        mAdapter = new RouteArrayAdapter(getActivity(), rotas);
        routesSpinner.setAdapter(mAdapter);
    }


    /**
     * A lista de paradas - próximas à localização atual - das rotas é preenchida de acordo com a rota selecionada
     * @param parent
     * @param view
     * @param position
     * @param id
     */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedRoute = rotas.get(position);

        paradasDaRota = DBUtils.getParadasRota(getContext(), selectedRoute);

        if (mapFragment != null) {

            paradasProximas = new ArrayList<NearStop>(DBUtils.getNearStops(getContext(),
                    mapFragment.getLat(), mapFragment.getLon(), Constants.NEAR_STOPS_RADIUS, null));

            paradasDisponiveis = new ArrayList<>();

            for (NearStop nearStop : paradasProximas) {
                if (paradasDaRota.contains(nearStop)) {
                    ParseUtils.getStopTime(getContext(), selectedRoute, nearStop, this);
                }
            }

        }
    }

    /**
     * OnClick do DropDown de rotas
     * @param v
     */
    @Override
    public void onClick(View v) {
        try {
            StopHeadsign stop = ((StopHeadsign) stopsSpinner.getSelectedItem());

            mCallback.onClickTakeBusButton(stop);
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.msg_no_stops_near, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public Route getSelectedRoute() {
        return selectedRoute;
    }

    public void setRoute(Route shortName) {
        selectedRouteName = shortName;
    }

    ///GPS ///
    @Override
    public void onConnected(Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (lastLocation == null) {
            requestLocationUpdates();
        } else {
            mLocation = lastLocation;
            Log.d(TAG, "aqui ja tem a localização" + mLocation);
            setRouteAdapter();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * Requisita ao sistema atualização da localização do usuario
     */
    public void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(Constants.LOCATION_REQUEST_INTERVAL)
                .setFastestInterval(Constants.DETECTION_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, getLocationCallback(), null);
    }

    private LocationCallback getLocationCallback() {
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    super.onLocationResult(result);
                    onMeliorLocationAvaliable(result.getLastLocation());
                    stopRequestLocationUpdates();
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                }
            };
        }

        return locationCallback;
    }

    private void onMeliorLocationAvaliable(Location lastLocation) {
        mLocation = lastLocation;
    }

    public void stopRequestLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationCallback());
    }

    protected Location getLocation() {
        return mLocation;
    }

    /**
     * Verifica se existe conexão com gps
     */
    /*private boolean checkGps() {
        LocationManager locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (((MelhorBusaoActivity) getActivity()).isLocationEnabled()) {
            return true;
        }
        return false;
    }*/

    /**
     * Seleciona as rotas que passam nas paradas próximas.
     *
     * @return Um conjunto de rotas próximas.
     */
    public void getNearRoutes() {
        if (mapFragment != null) {
            paradasProximas = new ArrayList<NearStop>(DBUtils.getNearStops(getContext(),
                    mapFragment.getLat(), mapFragment.getLon(), Constants.NEAR_STOPS_RADIUS, null));

            Set<Route> availableRoutes = StopRouteUtils.getRoutesFromStops(new TreeSet<NearStop>(paradasProximas));
            rotas = new ArrayList<Route>(availableRoutes);

        } else {
            rotas = new ArrayList<Route>();
        }
    }

    @Override
    public void onStopTimesReady(List<StopTime> stopTimes, ParseException e) {

    }

    /**
     * Muda o adapter de paradas da rota quando a lista com as paradas que vem do banco está pronta
     * @param stopHeadsignObj
     * @param e
     */
    @Override
    public void onStopHeadsignReady(StopHeadsign stopHeadsignObj, ParseException e) {
        paradasDisponiveis.add(stopHeadsignObj);
        if (paradasDisponiveis.size() == 0) {
            Toast.makeText(getContext(), R.string.msg_no_bus_near, Toast.LENGTH_SHORT).show();
            stopsSpinner.setBackgroundColor(Color.parseColor("#F3F3F3"));
        }

        mAdapterStop = new StopArrayAdapter(getActivity(), paradasDisponiveis, selectedRoute);
        stopsSpinner.setAdapter(mAdapterStop);


        if (paradasDisponiveis == null || paradasDisponiveis.isEmpty()) {
            takeBusButton.setEnabled(false);
            takeBusButton.setBackgroundColor(Color.parseColor("#DCDCDC"));
        } else {
            takeBusButton.setEnabled(true);
            takeBusButton.setTextColor(Color.parseColor("#F4F4FA"));
            takeBusButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

    }

    public void setAddress(String address) {
        addressField = (TextView) mView.findViewById(R.id.address_field);
        addressField.setText(address);
    }

    @Override
    public void onStopTimesReady(List<StopTime> stopTimes) {

    }

    public boolean onQueryTextSubmit(String query) {
        if (!((MelhorBusaoActivity) getActivity()).checkInternetConnection()){
            Toast.makeText(getContext(),R.string.msg_search_needs_internet, Toast.LENGTH_LONG).show();
            return false;

        }else{
            try {
                mapFragment.setAddress(query);
                try {
                    Geocoder geocoder;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                    LatLng point = new LatLng(geocoder.getFromLocationName(mapFragment.getAddress() + "," + Constants.CITY, 1).get(0).getLatitude(),
                            geocoder.getFromLocationName(mapFragment.getAddress() + "," + Constants.CITY, 1).get(0).getLongitude());
                    List<Address> addresses;
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                    if (addresses.get(0).getLocality().compareTo(Constants.CITY) == 0) {
                        mapFragment.updateMark(point);
                        mMenu.findItem(R.id.action_search).collapseActionView();
                        return true;
                    } else {
                        Toast.makeText(getActivity(), R.string.msg_failed_locate_search, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("NearStopsFragment", e.getMessage());
                    Toast.makeText(getActivity(), R.string.address_not_found_toast, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.e("NearStopsFragment", "Não foi possível achar a localizacao: " + e.getMessage());
                Toast.makeText(getActivity(), R.string.address_not_found_toast, Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    public interface OnTakeBusSelectedListener extends FragmentTitleChangeListener {
        void onClickTakeBusButton(StopHeadsign stopHeadsign);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

}




