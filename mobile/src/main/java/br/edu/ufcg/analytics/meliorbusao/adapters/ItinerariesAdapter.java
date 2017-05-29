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

        if (v == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            v = inflater.inflate(R.layout.get_directions_list_item, null);
        }

        TextView busCodesTextView = (TextView) v.findViewById(R.id.itinerary_bus_codes);
        TextView durationTextView = (TextView) v.findViewById(R.id.itinerary_duration);
        TextView stEndTimeTextView = (TextView) v.findViewById(R.id.itinerary_list_item_st_end_time);
        TextView stBusStopTextView = (TextView) v.findViewById(R.id.itinerary_list_item_start_bus_stop);
        TextView crowdLevelTextView = (TextView) v.findViewById(R.id.itinerary_crowd_level);

        try {
            busCodesTextView.setText(StringUtils.getStringListConcat(currItinerary.getBusRoutes()));

            String durationStr = "";
            if (currItinerary.getBtrDuration() != -1) {
                double durationInMin = currItinerary.getBtrDuration();
                if (durationInMin < 60) {
                    durationStr = String.valueOf((int)durationInMin) + "m";
                } else {
                    durationStr = String.valueOf((int)(durationInMin/60)) + "h" +
                            String.valueOf((int)(durationInMin%60)) + "m";
                }
            }
            durationTextView.setText(durationStr);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            stEndTimeTextView.setText(sdf.format(currItinerary.getDepartureTime()) + " - " +
                    sdf.format(currItinerary.getArrivalTime()));

            stBusStopTextView.setText(currItinerary.getDepartureBusStop());

            String crowdLevel = "";
            double numPassengers = currItinerary.getBtrNumPassengers();
            if (numPassengers != -1) {
                if (numPassengers <= 15) {
                    crowdLevel = "B";
                } else if (numPassengers <= 30) {
                    crowdLevel = "M";
                } else {
                    crowdLevel = "A";
                }
            }
            crowdLevelTextView.setText(crowdLevel);

        } catch (Exception e) {
            Log.e("ItinerariesListAdapter", e.getMessage());
        }

        v.getBackground().setAlpha(0);
        return v;
    }
}