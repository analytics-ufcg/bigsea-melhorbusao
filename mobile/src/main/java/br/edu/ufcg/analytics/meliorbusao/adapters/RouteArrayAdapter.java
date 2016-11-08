package br.edu.ufcg.analytics.meliorbusao.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.Route;

public class RouteArrayAdapter extends ArrayAdapter<Route> {

    private List<Route> items;
    private Activity activity;

    public RouteArrayAdapter(Activity activity, List<Route> items) {
        super(activity, android.R.layout.simple_list_item_1, items);
        this.items = items;
        this.activity = activity;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = getView(position, convertView, parent);
        v.getBackground().setAlpha(255);
        return v;
    }

    /**
     * Seleciona a rota desejada na activity de pegar o busão
     * @param position
     * @return
     */
    @Override
    public Route getItem(int position) {
        return items.get(position);
    }


    /**
     * Monta a interface de Pegar Busão / Proximos Horários
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Route currRoute = items.get(position);

        if (v == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            v = inflater.inflate(R.layout.search_suggestion_list_item, null);
        }

        TextView routeNameTxtView = (TextView) v.findViewById(R.id.route_name_text_view);
        routeNameTxtView.setText(currRoute.getShortName());

        ImageView busIcon = (ImageView) v.findViewById(R.id.suggestion_bus_icon);
        busIcon.setImageResource(R.drawable.ic_melior_busao);

        ImageView circle = (ImageView) v.findViewById(R.id.circle);
        circle.setColorFilter(Color.parseColor("#" + currRoute.getColor()));
        circle.setAlpha(160);

        TextView routeLongNameTxtView = (TextView)v.findViewById(R.id.route_long_name_text_view);

        String[] routeMainStops = currRoute.getMainStops().split(" - ");

        String ida = routeMainStops[0];
        String volta = routeMainStops[1];


        TextView idaView = (TextView) v.findViewById(R.id.route_long_name_text_view);
        idaView.setText(ida);
        TextView voltaView = (TextView) v.findViewById(R.id.route_long_name_text_view_volta);
        voltaView.setText(volta);

        ImageView rightArrow = (ImageView) v.findViewById(R.id.right_arrow);
        rightArrow.setColorFilter(Color.parseColor("#" + currRoute.getColor()));

        v.getBackground().setAlpha(0);
        return v;
    }
}
