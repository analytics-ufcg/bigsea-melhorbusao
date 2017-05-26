package br.edu.ufcg.analytics.meliorbusao.adapters;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;
import br.edu.ufcg.analytics.meliorbusao.models.btr.BTRResponse;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;
import br.edu.ufcg.analytics.meliorbusao.models.otp.ItineraryLeg;
import br.edu.ufcg.analytics.meliorbusao.utils.StringUtils;

public class ItinerariesAdapter extends ArrayAdapter<Itinerary> {
    private List<Itinerary> items;
    private Activity activity;

    public ItinerariesAdapter(Activity activity, List<Itinerary> items) {
        super(activity, R.layout.get_directions_list_item, items);
        this.items = items;
        this.activity = activity;
    }

    /**
     * Define o adapter como um componente dropdown (para que o dropdown seja possivel)
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = getView(position, convertView, parent);
        v.getBackground().setAlpha(255);
        return v;
    }

    /**
     * Retorna o item (parada a ser escolhida) do dropdown
     */
    @Override
    public Itinerary getItem(int position) {
        return items.get(position);
    }

    /**
     * Monta a view com informações da parada a ser escolhida
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Itinerary currItinerary = items.get(position);

        new BTRTask(currItinerary,position).execute();

        if (v == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            v = inflater.inflate(R.layout.get_directions_list_item, null);
        }

        TextView busCodesTextView = (TextView) v.findViewById(R.id.itinerary_bus_codes);
        TextView durationTextView = (TextView) v.findViewById(R.id.itinerary_duration);
        TextView stEndTimeTextView = (TextView) v.findViewById(R.id.itinerary_list_item_st_end_time);
        TextView stBusStopTextView = (TextView) v.findViewById(R.id.itinerary_list_item_start_bus_stop);

        try {
            busCodesTextView.setText(StringUtils.getStringListConcat(currItinerary.getBusRoutes()));

            int durationInMins = currItinerary.getDurationInSecs()/60;
            durationTextView.setText(String.valueOf(durationInMins) + " min");

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            stEndTimeTextView.setText(sdf.format(currItinerary.getDepartureTime()) + " - " +
                    sdf.format(currItinerary.getArrivalTime()));

            stBusStopTextView.setText(currItinerary.getDepartureBusStop());
        } catch (Exception e) {
            Log.e("ItinerariesListAdapter", e.getMessage());
        }

        v.getBackground().setAlpha(0);
        return v;
    }

    public class BTRTask extends AsyncTask<Void, Void, List<BTRResponse> > {

        private final String ENDPOINT_ADDRESS = getContext().getString(R.string.BEST_TRIP_RECOMMENDER_URL) + "get_best_trips?";
        private Itinerary it;
        private int viewId;


        BTRTask(Itinerary it, int viewId) {
            this.it = it;
            this.viewId = viewId;
        }

        BTRResponse getItineraryLegBTRPrediction(ItineraryLeg itLeg) {
            BTRResponse btrResp = null;
            URL url;
            String responseMessage = "";

            try {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("MM/dd/yyyy");
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
            return btrResp;
        }

        @Override
        protected List<BTRResponse> doInBackground(Void... params) {
            List<BTRResponse> btrResponses = new ArrayList<BTRResponse>();

            for (ItineraryLeg itLeg : it.getLegs()) {
                if (itLeg.getMode() == ItineraryLeg.LEG_MODE_BUS) {
                    btrResponses.add(getItineraryLegBTRPrediction(itLeg));
                }
            }

            return btrResponses;
        }

        @Override
        protected void onPostExecute(List<BTRResponse> btrResponses) {
            double itBusTripDuration = 0.0, itBusNumPassengers = Double.MIN_VALUE, itWalkTripDuration = 0.0;

            for (BTRResponse btrResp : btrResponses) {
                itBusTripDuration += btrResp.getTripDuration();
                itBusNumPassengers = Math.max(itBusNumPassengers, btrResp.getPassengersNum());
            }

            Itinerary currIt = getItem(viewId);

            for (ItineraryLeg itLeg : currIt.getLegs()) {
                if (itLeg.getMode() == ItineraryLeg.LEG_MODE_WALK) {
                    itWalkTripDuration += ((itLeg.getEndTime().getTime() - itLeg.getStartTime().getTime())/1000)/60;
                }
            }

            currIt.setDurationInSecs((int)(itWalkTripDuration + itBusTripDuration));
            currIt.setNumPassengers(itBusNumPassengers);
            notifyDataSetChanged();
        }
    }

//    public class BTRTask extends AsyncTask<Void, Void, BTRResponse> {
//
//        private final String ENDPOINT_ADDRESS = getString(R.string.BEST_TRIP_RECOMMENDER_URL) + "get_best_trips?";
//        private final String route;
//        private final String time;
//        private final String date;
//        private final int busStopId;
//        private String responseMessage = "";
//        private BTRResponse btrResp;
//
//        BTRTask(String route, Date date, int busStopId) {
//            this.route = route;
//            SimpleDateFormat sdf = new SimpleDateFormat();
//            sdf.applyPattern("MM/dd/yyyy");
//            this.date = sdf.format(date);
//            sdf.applyPattern("HH:mm:ss");
//            this.time = sdf.format(date);
//            this.busStopId = busStopId;
//        }
//
//        @Override
//        protected BTRResponse doInBackground(Void... params) {
//            URL url;
//            try {
//                StringBuilder parameters = new StringBuilder();
//                parameters.append("route=");
//                parameters.append(route);
//                parameters.append("&time=");
//                parameters.append(time);
//                parameters.append("&date=");
//                parameters.append(date);
//                parameters.append("&bus_stop_id=");
//                parameters.append(busStopId);
//                parameters.append("&closest_trip_type=next_hour");
//
//                Log.d("SearchScheduleFragment", parameters.toString());
//
//                url = new URL(ENDPOINT_ADDRESS + parameters.toString());
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//                conn.setRequestProperty("Accept", "application/json");
//                conn.setRequestProperty("Content-Type", "application/json");
//
//                conn.connect();
//                int responseCode = conn.getResponseCode();
//
//                Log.d("SearchScheduleFragment", "Response Code: " + String.valueOf(responseCode));
//
//                if (responseCode == HttpsURLConnection.HTTP_OK) {
//                    String line = "";
//                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    while ((line = br.readLine()) != null) {
//                        responseMessage += line;
//                    }
//                    JSONArray response = new JSONArray(responseMessage);
//                    JSONObject tripJson = response.getJSONObject(0);
//                    btrResp = BTRResponse.fromJson(tripJson);
//
//                    Log.d("SearchScheduleFragment", "BTR Prediction - numPass:" +
//                            btrResp.getPassengersNum() + " tripDur: " + btrResp.getTripDuration());
//                } else {
//                    BufferedReader br1 = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//                    String line = "", error = "";
//                    while ((line = br1.readLine()) != null) {
//                        error += line;
//                    }
//                    Log.d("SearchScheduleFragment", error);
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return btrResp;
//        }
//
//        @Override
//        protected void onPostExecute(BTRResponse btrResp) {
//            View item = mItineraryListView.getChildAt(0);
//        }
//    }
}
