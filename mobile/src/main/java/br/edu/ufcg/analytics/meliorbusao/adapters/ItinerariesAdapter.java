package br.edu.ufcg.analytics.meliorbusao.adapters;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;
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
}
