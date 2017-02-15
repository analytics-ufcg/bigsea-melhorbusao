package br.edu.ufcg.analytics.meliorbusao.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.Route;

public class InfoWindowAdapter extends BaseAdapter {

    private Context mContext;
    private List<Route> routes;

    public InfoWindowAdapter(Context c, List<Route> routes) {
        mContext = c;
        this.routes = routes;
    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public Object getItem(int position) {
        return routes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridItemview;
        ImageView circle;
        TextView routeNameTextView;
        Route route;
        String circleOpacity = "#FF";

        gridItemview = new View(mContext);
        gridItemview = inflater.inflate(R.layout.info_window_grid_item, null);
        circle = (ImageView) gridItemview.findViewById(R.id.circle);
        routeNameTextView = (TextView) gridItemview.findViewById(R.id.route_name_field);
        route = routes.get(position);

        circle.setColorFilter(Color.parseColor(circleOpacity + route.getColor()));
        routeNameTextView.setText(route.getId());

        return gridItemview;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
}
