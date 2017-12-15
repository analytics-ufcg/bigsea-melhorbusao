package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.adapters.RoutesAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.StopsAdapter;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnMapInformationReadyListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnStopTimesReadyListener;
import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;
import br.edu.ufcg.analytics.meliorbusao.utils.JsonBTRUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

public class SearchScheduleFragment extends Fragment implements OnStopTimesReadyListener,
        SearchView.OnQueryTextListener, OnMapInformationReadyListener {

    public static final String TAG = "SearchScheduleFragment";
    public static final String SELECTED_ROUTE_KEY = "selectedRouteKey";
    private static SearchScheduleFragment instance;
    private ArrayList<StopHeadsign> paradasDisponiveis;
    private Button getDirectionsButton;
    private LatLng mDestLocation;
    private LatLng mCurrLocation;
    private MapFragment mMapFragment;
    private Menu mMenu;
    private RoutesAdapter mRoutesAdapter;
    private GetDirectionsListener mCallback;
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
        View mView = inflater.inflate(R.layout.fragment_get_directions, container, false);
        getChildFragmentManager().beginTransaction().replace(R.id.get_directions_map_fragment, mMapFragment).commit();

        addressField = (TextView) mView.findViewById(R.id.address_field);

        /*mProgressView = getActivity().findViewById(R.id.login_progress);*/

        getDirectionsButton = (Button) mView.findViewById(R.id.show_schedule_button);
//        getDirectionsButton.setEnabled(false);
        getDirectionsButton.setClickable(true);
        getDirectionsButton.setBackgroundColor(getResources().getColor(R.color.inactiveIconColorDark));
        getDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click");
                /*showProgress(true);*/
                if (mCurrLocation== null ||mDestLocation==null ){
                    Toast.makeText(getContext(), R.string.search_schedule_btn, Toast.LENGTH_LONG).show();

                } else{
                    String cityCode = SharedPreferencesUtils.getCityNameOnDatabase(getContext()).
                            equals("Curitiba")? "ctba" : "cg";
                    mAuthTask = new RoutingTask(mCurrLocation, mDestLocation, new Date(), cityCode);
                    getDirectionsButton.setClickable(false);
                    mAuthTask.execute();
                }


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
        ((MelhorBusaoActivity) getActivity()).getSupportActionBar().setTitle(R.string.get_directions_title);
        if (!((MelhorBusaoActivity) getActivity()).isLocationEnabled()) {
            ((MelhorBusaoActivity) getActivity()).buildAlertMessageNoGps();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (GetDirectionsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SearchScheduleListener");
        }
    }

    @Override
    public void onStopHeadsignReady(StopHeadsign stopHeadsignObj, ParseException e) {

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
                String cityName = SharedPreferencesUtils.getCityNameOnDatabase(getContext());
                Address queryResultAddress =  geocoder.getFromLocationName(query + "," + cityName, 1).get(0);
                mDestLocation = new LatLng(queryResultAddress.getLatitude(),queryResultAddress.getLongitude());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(mDestLocation.latitude, mDestLocation.longitude, 1);
                if (addresses.get(0).getLocality().compareTo(cityName) == 0) {
                    mMapFragment.updatePlaceMarker(new GeoPoint(mDestLocation.latitude, mDestLocation.longitude));
                    mMenu.findItem(R.id.action_search).collapseActionView();
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
        mCurrLocation = new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude());
    }

    /**
     * Updates the list of nearby routes accordint to the point tapped in the map.
     * @param geoPoint The representation of the point where the map has been tapped.
     */
    @Override
    public void onMapClick(GeoPoint geoPoint) {
        mDestLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    public interface SearchScheduleListener extends FragmentTitleChangeListener {
        void onClickTakeBusButton(StopHeadsign stopHeadsign);
    }

    public interface GetDirectionsListener extends FragmentTitleChangeListener {
        void onGetDirectionsButtonClick(List<Itinerary> itineraries);
    }

    public class RoutingTask extends AsyncTask<Void, Void, List<Itinerary>> {

        private final String ENDPOINT_ADDRESS = getString(R.string.OPEN_TRIP_PLANNER_URL) + "/btr_routes_plans";
        private final String fromPlace;
        private final String toPlace;
        private final String date;
        private final String time;
        private final String cityCode;
        private String responseMessage = "";
        private List<Itinerary> itineraries;

        RoutingTask(LatLng origCoords, LatLng destCoords, Date date, String cityCode) {
            this.fromPlace = String.valueOf(origCoords.latitude) + "," + String.valueOf(origCoords.longitude);
            this.toPlace = String.valueOf(destCoords.latitude) + "," + String.valueOf(destCoords.longitude);
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("MM/dd/yyyy");
            this.date = sdf.format(date);
            sdf.applyPattern("HH:mm:ss");
            this.time = sdf.format(date);
            this.cityCode = cityCode;
            itineraries = new ArrayList<Itinerary>();
        }

        @Override
        protected List<Itinerary> doInBackground(Void... params) {
            URL url;
            boolean success = false;
            try {
                url = new URL(ENDPOINT_ADDRESS);

                Map<String, String> param = new HashMap<>();

                param.put("fromPlace", fromPlace);
                param.put("toPlace", toPlace);
                param.put("mode", "TRANSIT,WALK");
                param.put("date", date);
                param.put("time", time);

                Gson gson = new Gson(); // com.google.gson.Gson
                String json = gson.toJson(param);

                //Log.d(TAG, json.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json.toString());
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                int responseCode = conn.getResponseCode();


                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    String line = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        responseMessage += line;
                    }
                    JSONObject response = new JSONObject(responseMessage);
                    itineraries = JsonBTRUtils.itinerariesFromJson(response);

                } else {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String line = "", error = "";
                    while ((line = br1.readLine()) != null) {
                        error += line;
                    }
                    Log.d(TAG, "Error: " + error);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return itineraries;
        }

        @Override
        protected void onPostExecute(List<Itinerary> itineraries) {
            mCallback.onGetDirectionsButtonClick(itineraries);
        }
    }
}
