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

public class RoutesAdapter extends ArrayAdapter<Route> {

    private List<Route> routes;
    private Activity activity;

    public RoutesAdapter(Activity activity, List<Route> routes) {
        super(activity, android.R.layout.simple_list_item_1, routes);
        this.routes = routes;
        this.activity = activity;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = getView(position, convertView, parent);
        v.getBackground().setAlpha(255);
        return v;
    }

    @Override
    public Route getItem(int position) {
        return routes.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Route currRoute = routes.get(position);

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

    public int getRouteIndexByName(String routeName) {
        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).getShortName().equals(routeName)) {
                return i;
            }
        }
        return -1;
    }
}
