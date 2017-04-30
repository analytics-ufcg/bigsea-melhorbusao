package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.HttpsURLConnection;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.adapters.RoutesAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.StopsAdapter;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnMapInformationReadyListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnStopTimesReadyListener;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.Stop;
import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;
import br.edu.ufcg.analytics.meliorbusao.utils.ParseUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.StopRouteUtils;

public class SearchScheduleFragment extends Fragment implements OnStopTimesReadyListener,
        SearchView.OnQueryTextListener, OnMapInformationReadyListener {

    public static final String TAG = "SearchScheduleFragment";
    public static final String SELECTED_ROUTE_KEY = "selectedRouteKey";
    private static SearchScheduleFragment instance;
    private ArrayList<StopHeadsign> paradasDisponiveis;
    private Button showScheduleButton;
    private LatLng mCoordinates;
    private MapFragment mMapFragment;
    private Menu mMenu;
    private RoutesAdapter mRoutesAdapter;
    private SearchScheduleListener mCallback;
    private SearchView mSearchView;
    private Spinner routesSpinner;
    private Spinner stopsSpinner;
    private StopsAdapter mStopsAdapter;
    private TextView addressField;

    private RoutingTask mAuthTask = null;
    /*private View mProgressView;*/

    public SearchScheduleFragment() {
    }

    /**
     * Returns one instance of SearchScheduleFragment (it guarantees that there is only one
     * SearchScheduleFragment object at the same time)
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
        mMapFragment = new MapFragment();
        mMapFragment.setOnMapInformationReadyListener(this);
        mMapFragment.enableFetchAddressService();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_search_schedule, container, false);
        getChildFragmentManager().beginTransaction().replace(R.id.melhor_map_fragment, mMapFragment).commit();

        paradasDisponiveis = new ArrayList<>();
        initializeRoutesSpinner(mView);

        stopsSpinner = (Spinner) mView.findViewById(R.id.schedules_stops_spinner);
        mStopsAdapter = new StopsAdapter(getActivity(), new ArrayList<StopHeadsign>(), null);
        stopsSpinner.setAdapter(mStopsAdapter);

        addressField = (TextView) mView.findViewById(R.id.address_field);

        /*mProgressView = getActivity().findViewById(R.id.login_progress);*/

        showScheduleButton = (Button) mView.findViewById(R.id.show_schedule_button);
        showScheduleButton.setEnabled(false);
        showScheduleButton.setBackgroundColor(getResources().getColor(R.color.inactiveIconColorDark));
        showScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*showProgress(true);*/
                mAuthTask = new RoutingTask(-25.39211,-49.22613,-25.45102,-49.28381);
                mAuthTask.execute();
                /*try {
                    StopHeadsign stop = ((StopHeadsign) stopsSpinner.getSelectedItem());
                    mCallback.onClickTakeBusButton(stop);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.msg_no_stops_near, Toast.LENGTH_LONG).show();
                }*/

            }
        });

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
        ((MelhorBusaoActivity) getActivity()).getSupportActionBar().setTitle(R.string.bus_schedule_title);
        if (!((MelhorBusaoActivity) getActivity()).isLocationEnabled()) {
            ((MelhorBusaoActivity) getActivity()).buildAlertMessageNoGps();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (SearchScheduleListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SearchScheduleListener");
        }
    }

    /**
     * Initialize the spinner that shows nearby routes.
     * @param mView The container view of the spinner
     */
    private void initializeRoutesSpinner(View mView) {
        routesSpinner = (Spinner) mView.findViewById(R.id.schedules_routes_spinner);
        mRoutesAdapter = new RoutesAdapter(getActivity(), new ArrayList<Route>());
        routesSpinner.setAdapter(mRoutesAdapter);
        routesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paradasDisponiveis.clear();
                Route selectedRoute = mRoutesAdapter.getItem(position);
                Set<Stop> paradasDaRota = DBUtils.getParadasRota(getContext(), selectedRoute);
                Set<NearStop> nearbyStops = findNearbyStops(new LatLng(mCoordinates.latitude, mCoordinates.longitude));
                for (NearStop nearStop : nearbyStops) {
                    if (paradasDaRota.contains(nearStop)) {
                        ParseUtils.getStopTime(selectedRoute, nearStop, SearchScheduleFragment.this);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Find routes that pass within a five hundred meters radius from the coordinates.
     * @param coordinates The central point for calculating the area.
     * @return A List with all the nearby routes.
     */
    private List<Route> findNearbyRoutes(LatLng coordinates) {
        Set<NearStop> nearbyStops = findNearbyStops(coordinates);
        Set<Route> availableRoutes = StopRouteUtils.getRoutesFromStops(new TreeSet<>(nearbyStops));
        List<Route> nearbyRoutes = new ArrayList<>(availableRoutes);
        Collections.sort(nearbyRoutes);
        return nearbyRoutes;
    }

    /**
     * Find bus stops located within a five hundred meters radius from the given coordinates.
     * @param coordinates The central point for calculating the area.
     * @return A List with all the nearby stops.
     */
    private Set<NearStop> findNearbyStops(LatLng coordinates) {
        Set<NearStop> nearbyStops = new TreeSet<>(DBUtils.getNearStops(getContext(), coordinates.latitude,
                coordinates.longitude, Constants.NEAR_STOPS_RADIUS, null));
        return  nearbyStops;
    }

    /**
     * Executes an action every time a StopHeadSignObj is loaded from the server. The StopHeadSignObjs
     * are loaded each one at a time for each nearby stop for the selected nearby route.
     *
     * @param stopHeadsignObj The object loaded by the server.
     * @param e An exception that might have been thrown.
     */
    @Override
    public void onStopHeadsignReady(StopHeadsign stopHeadsignObj, ParseException e) {
        paradasDisponiveis.add(stopHeadsignObj);
        if (paradasDisponiveis.isEmpty()) {
            stopsSpinner.setBackgroundColor(Color.parseColor("#F3F3F3"));
            showScheduleButton.setEnabled(false);
            showScheduleButton.setBackgroundColor(Color.parseColor("#DCDCDC"));
        } else {
            showScheduleButton.setEnabled(true);
            showScheduleButton.setTextColor(Color.parseColor("#F4F4FA"));
            showScheduleButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        mStopsAdapter.clear();
        mStopsAdapter.addAll(paradasDisponiveis);
        mStopsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStopTimesReady(List<StopTime> stopTimes) {
    }

    /**
     * Marks a new address in the map for the address given as a query and also load the nearby routes
     * for the address given.
     * @param query A string representing the address for look up.
     * @return A boolean that indicates wether the address was found (true) or not (false).
     */
    public boolean onQueryTextSubmit(String query) {
        if (!((MelhorBusaoActivity) getActivity()).checkInternetConnection()) {
            Toast.makeText(getContext(), R.string.msg_search_needs_internet, Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                Geocoder geocoder;
                geocoder = new Geocoder(getContext(), Locale.getDefault());
                mCoordinates = new LatLng(geocoder.getFromLocationName(query + "," + Constants.CITY, 1).get(0).getLatitude(),
                        geocoder.getFromLocationName(query + "," + Constants.CITY, 1).get(0).getLongitude());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(mCoordinates.latitude, mCoordinates.longitude, 1);
                if (addresses.get(0).getLocality().compareTo(Constants.CITY) == 0) {
                    mMapFragment.updatePlaceMarker(new GeoPoint(mCoordinates.latitude, mCoordinates.longitude));
                    mMenu.findItem(R.id.action_search).collapseActionView();
                    List<Route> nearbyRoutes = findNearbyRoutes(mCoordinates);
                    mRoutesAdapter.clear();
                    mRoutesAdapter.addAll(nearbyRoutes);
                    mRoutesAdapter.notifyDataSetChanged();
                    routesSpinner.setAdapter(mRoutesAdapter);
                    return true;
                } else {
                    Toast.makeText(getActivity(), R.string.msg_failed_locate_search, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(getActivity(), R.string.address_not_found_toast, Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    /**
     * Updates the address TextView whenever a new address is available
     * @param mapAddres The full address.
     */
    @Override
    public void onMapAddressFetched(String mapAddres) {
        addressField.setText(mapAddres);
    }

    /**
     * Updates the list of nearby routes whenever a new location is available
     */
    @Override
    public void onMapLocationAvailable(Location mapLocation) {
        mCoordinates = new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude());
        List<Route> nearbyRoutes = findNearbyRoutes(mCoordinates);
        mRoutesAdapter.clear();
        mRoutesAdapter.addAll(nearbyRoutes);
        mRoutesAdapter.notifyDataSetChanged();

        String selectedRouteName = (String) this.getArguments().get(SELECTED_ROUTE_KEY);
        if (selectedRouteName != null) {
            int index = mRoutesAdapter.getRouteIndexByName(selectedRouteName);
            if (index < 0){
                Toast.makeText(getContext(), R.string.msg_no_stops_for_bus, Toast.LENGTH_LONG).show();
            } else {
                routesSpinner.setSelection(index);
            }
        }
        this.getArguments().putString(SELECTED_ROUTE_KEY, null);
    }

    /**
     * Updates the list of nearby routes accordint to the point tapped in the map.
     * @param geoPoint The representation of the point where the map has been tapped.
     */
    @Override
    public void onMapClick(GeoPoint geoPoint) {
        mCoordinates = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        List<Route> nearbyRoutes = findNearbyRoutes(mCoordinates);
        mRoutesAdapter.clear();
        mRoutesAdapter.addAll(nearbyRoutes);
        mRoutesAdapter.notifyDataSetChanged();
        routesSpinner.setAdapter(mRoutesAdapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    public interface SearchScheduleListener extends FragmentTitleChangeListener {
        void onClickTakeBusButton(StopHeadsign stopHeadsign);
    }

    public class RoutingTask extends AsyncTask<Void, Void, String> {

        private static final String ENDPOINT_ADDRESS = "http://150.165.85.4:10402/otp/routers/default/plan?";
        private final String fromPlace;
        private final String toPlace;
        private String responseMessage = "";

        RoutingTask(double fromPlaceLat, double fromPlaceLong, double toPlaceLat, double toPlaceLong) {
            this.fromPlace = String.valueOf(fromPlaceLat) + "," + String.valueOf(fromPlaceLong);
            this.toPlace = String.valueOf(toPlaceLat) + "," + String.valueOf(toPlaceLong);
        }

        @Override
        protected String doInBackground(Void... params) {
            URL url;
            try {
                StringBuilder parameters = new StringBuilder();
                parameters.append("fromPlace=");
                parameters.append(fromPlace);
                parameters.append("&toPlace=");
                parameters.append(toPlace);
                parameters.append("&mode=TRANSIT,WALK");
                parameters.append("&date=04/03/2017");
                parameters.append("&time=16:20:00");

                Log.d("SearchScheduleFragment", parameters.toString());

                url = new URL(ENDPOINT_ADDRESS + parameters.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");

                /*OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(parameters.toString());
                writer.flush();
                writer.close();
                os.close();*/

                conn.connect();
                int responseCode = conn.getResponseCode();

                Log.d("SearchScheduleFragment", "Response Code: " + String.valueOf(responseCode));

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        responseMessage += line;
                    }
                    JSONObject response = new JSONObject(responseMessage);
                    JSONObject plan = response.getJSONObject("plan");
                    JSONArray itinerariesJson = plan.getJSONArray("itineraries");

                    Log.d("SearchScheduleFragment", "Number of itineraries: " + String.valueOf(itinerariesJson.length()));
                    Log.d("SearchScheduleFragment", "First Itinerary: " + itinerariesJson.getJSONObject(1).toString());
                } else {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String line = "", error = "";
                    while ((line = br1.readLine()) != null) {
                        error += line;
                    }

                    Log.d("SearchScheduleFragment", error);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return responseMessage;
        }
    }

    /**
     * Shows the progress UI and hides the isSuccessfulLogin form.
     */
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }*/
}
