package br.edu.ufcg.analytics.meliorbusao.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import br.edu.ufcg.analytics.meliorbusao.R;

public class StopInfoAdapter extends Fragment implements GoogleMap.InfoWindowAdapter {

    Activity mActivity;

    public StopInfoAdapter() {
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
    public void setActivity(Activity activity){
        mActivity = activity;
    }

    /**
     * Monta o tooltip das informações das paradas em Paradas Proximas e Mapa de Rotas
     * @param marker
     * @return
     */
    @Override
    public View getInfoContents(Marker marker) {
        View v = mActivity.getLayoutInflater().inflate(R.layout.stop_info, null);
        TextView title_street = (TextView) v.findViewById(R.id.title_street);
        TextView title_stopName = (TextView) v.findViewById(R.id.title_stopName);
        int col = 1;

        LinearLayout stop = (LinearLayout) v.findViewById(R.id.title_stop);
        LinearLayout col1 = (LinearLayout) v.findViewById(R.id.col1);
        LinearLayout col2 = (LinearLayout) v.findViewById(R.id.col2);


        if (marker.getSnippet()!= null) {
            String[] routes = marker.getSnippet().split(",");
            HashMap<String, String> routeColors = new HashMap<>();
            String[] routeIdColor;
            String routeIds;
            for (String route : routes) {
                routeIdColor = route.split(";");
                if ((routeIds = routeColors.get(routeIdColor[1])) == null) {
                    routeColors.put(routeIdColor[1], routeIdColor[0]);
                } else {
                    routeColors.put(routeIdColor[1], (routeIds + " " + routeIdColor[0]).trim());
                }
            }

            for (String color : routeColors.keySet()) {
                View snippetLine = mActivity.getLayoutInflater().inflate(R.layout.snippet_line_item, null);
                TextView snippetLineText = (TextView) snippetLine.findViewById(R.id.snippet_stop);
                ImageView snippetLineImageView = (ImageView) snippetLine.findViewById(R.id.snippet_line_color);
                snippetLineImageView.setColorFilter(Color.parseColor("#" + color));

                snippetLineText.setText(routeColors.get(color));
                if (col == 1){
                    col1.addView(snippetLine);
                    col=2;

                } else {
                    col2.addView(snippetLine);
                    col=1;
                }
            }
        }

        String street = "";
        String stopName = "";
        try{
            street = marker.getTitle().substring(0, marker.getTitle().indexOf (" - "));
            stopName = getString(R.string.bus_stop)+ marker.getTitle().substring(marker.getTitle().indexOf (" - ") + 3);

        }catch (Exception e){
            street = marker.getTitle();
        }

/*
        if ((street + stopName).length() > 40 ){
            stop.setOrientation(LinearLayout.VERTICAL);
        } else {
            stop.setOrientation(LinearLayout.HORIZONTAL);
            street += " - ";
        }*/
        title_street.setText(street);

        /*
        // considera apenas paradas que iniciam com números
        if (stopName.substring(0, 1).matches("[0-9]")){
            stopName = "Parada " + stopName;
        } */

        title_stopName.setText(stopName);

        return v;
    }


}