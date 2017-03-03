package br.edu.ufcg.analytics.meliorbusao.adapters;


import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;

public class StopsAdapter extends ArrayAdapter<StopHeadsign> {
    private final Route routeSelected;
    private List<StopHeadsign> items;
    private Activity activity;

    public StopsAdapter(Activity activity, List<StopHeadsign> items, Route routeSelected) {
        super(activity, android.R.layout.simple_list_item_1, items);
        this.routeSelected = routeSelected;
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
    public StopHeadsign getItem(int position) {
        return items.get(position);
    }

    /**
     * Monta a view com informações da parada a ser escolhida
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        StopHeadsign currStopHeadsign = items.get(position);

        if (v == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            v = inflater.inflate(R.layout.spinner_stops_take_bus, null);
        }

        TextView textView = (TextView) v.findViewById(R.id.stop_name);

        try {
            textView.setText(currStopHeadsign.getNearStops().getName());
        } catch (Exception e) {
            Log.e("currStopHeadsign.getNearStops", e.getMessage());
        }

        try {
            // Adiciona o sentido da parada
            TextView mainText = (TextView) v.findViewById(R.id.stop_direction);
            String direction = getContext().getResources().getString(R.string.direction) + currStopHeadsign.getStopTime().getStopHeadsign();
            mainText.setText(direction);
        } catch (Exception e) {
            e.printStackTrace();
        }

        v.getBackground().setAlpha(0);
        return v;
    }
}
