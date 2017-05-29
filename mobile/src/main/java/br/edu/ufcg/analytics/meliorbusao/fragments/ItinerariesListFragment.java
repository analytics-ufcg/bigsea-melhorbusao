package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.SchedulesProbableParser;
import br.edu.ufcg.analytics.meliorbusao.adapters.ItinerariesAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.RoutesAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.ScheduleAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.StopsAdapter;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;
import br.edu.ufcg.analytics.meliorbusao.models.btr.BTRResponse;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;
import br.edu.ufcg.analytics.meliorbusao.models.otp.ItineraryLeg;


public class ItinerariesListFragment extends Fragment {

    private static ItinerariesListFragment instance;

    private View mView;
    private ListView mItineraryListView;

    private List<Itinerary> itineraries;

    public static final String TAG = "ITINERARIES_LIST_FRAGMENT";
    private FragmentTitleChangeListener mCallback;
    private ItinerariesAdapter mAdapter;

    public static ItinerariesListFragment getInstance() {
        if (instance == null) {
            ItinerariesListFragment fragment = new ItinerariesListFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            instance = fragment;
        }
        return instance;
    }

    public ItinerariesListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_itineraries_list, container, false);

        //SPAWN BTR QUERIES
        for (int i = 0; i < itineraries.size(); i++) {
            new BTRTask(itineraries.get(i),i).execute();
        }

        ////////ITINERARIES/////////
        mAdapter = new ItinerariesAdapter(getActivity(),itineraries);
        mItineraryListView = (ListView) mView.findViewById(R.id.itineraries_list);
        mItineraryListView.setAdapter(mAdapter);


        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (FragmentTitleChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallback.onTitleChange(getResources().getString(R.string.itineraries_list_title));
        //updateListView();
    }

    public void setItinerariesList(List<Itinerary> itineraries){
        this.itineraries = itineraries;
    }

    public class BTRTask extends AsyncTask<Void, Void, List<BTRResponse> > {

        private final String ENDPOINT_ADDRESS = getActivity().getString(R.string.BEST_TRIP_RECOMMENDER_URL) + "/get_best_trips?";
        private Itinerary it;
        private int viewPos;


        BTRTask(Itinerary it, int viewPos) {
            this.it = it;
            this.viewPos = viewPos;
        }

//        BTRResponse getItineraryLegBTRPrediction(ItineraryLeg itLeg) {
//
//        }

        @Override
        protected List<BTRResponse> doInBackground(Void... params) {
            List<BTRResponse> btrResponses = new ArrayList<BTRResponse>();

            for (ItineraryLeg itLeg : it.getLegs()) {
                if (itLeg.getMode().equals(ItineraryLeg.LEG_MODE_BUS)) {
                    BTRResponse btrResp = null;
                    URL url;
                    String responseMessage = "";

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat();
                        sdf.applyPattern("yyyy-MM-dd");
                        String date = sdf.format(itLeg.getStartTime());
                        sdf.applyPattern("HH:mm:ss");
                        String time = sdf.format(itLeg.getStartTime());

                        StringBuilder parameters = new StringBuilder();
                        parameters.append("route=");
                        parameters.append(itLeg.getRoute());
                        parameters.append("&time=");
                        parameters.append(time);
                        parameters.append("&date=");
                        parameters.append(date);
                        parameters.append("&bus_stop_id=");
                        parameters.append(itLeg.getFromStopId());
                        parameters.append("&closest_trip_type=single_trip");

                        Log.d("SearchScheduleFragment", parameters.toString());

                        url = new URL(ENDPOINT_ADDRESS + parameters.toString());

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setRequestProperty("Content-Type", "application/json");

                        conn.connect();
                        int responseCode = conn.getResponseCode();

                        Log.d("SearchScheduleFragment", "Response Code: " + String.valueOf(responseCode));

                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                            String line = "";
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            while ((line = br.readLine()) != null) {
                                responseMessage += line;
                            }
                            JSONArray response = new JSONArray(responseMessage);
                            JSONObject tripJson = response.getJSONObject(0);
                            btrResp = BTRResponse.fromJson(tripJson);

                            Log.d("SearchScheduleFragment", "BTR Prediction - numPass:" +
                                    btrResp.getPassengersNum() + " tripDur: " + btrResp.getTripDuration());
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
                    btrResponses.add(btrResp);
                }
            }

            return btrResponses;
        }

        @Override
        protected void onPostExecute(List<BTRResponse> btrResponses) {
            double itBusTripDuration = 0.0, itBusNumPassengers = Double.MIN_VALUE, itWalkTripDuration = 0.0;

            for (BTRResponse btrResp : btrResponses) {
                if (btrResp != null) {
                    itBusTripDuration += btrResp.getTripDuration();
                    itBusNumPassengers = Math.max(itBusNumPassengers, btrResp.getPassengersNum());
                }
            }

            Itinerary currIt = mAdapter.getItem(viewPos);

            for (ItineraryLeg itLeg : currIt.getLegs()) {
                if (itLeg.getMode().equals(ItineraryLeg.LEG_MODE_WALK)) {
                    itWalkTripDuration += ((itLeg.getEndTime().getTime() - itLeg.getStartTime().getTime())/60000);
                }
            }

            currIt.setBtrDuration(itWalkTripDuration + itBusTripDuration);
            currIt.setBtrNumPassengers(itBusNumPassengers);
            mAdapter.notifyDataSetChanged();
        }
    }
}
