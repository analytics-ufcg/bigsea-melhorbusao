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
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;


public class ScheduleAdapter extends ArrayAdapter<StopTime> {

    private List<StopTime> stopTimes;
    private Activity activity;

    private int secondFastest=Integer.MIN_VALUE;
    private int secondEmptiest=Integer.MIN_VALUE;



    public ScheduleAdapter(Activity activity, int resource, List<StopTime> stopTimes) {
        super(activity, resource, stopTimes);
        this.stopTimes = stopTimes;
        this.activity = activity;
        sort();
    }

    /**
     * Retorna o item (parada a ser escolhida) do dropdown
     *
     * @param position
     * @return
     */
    @Override
    public StopTime getItem(int position) {
        return stopTimes.get(position);
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
        StopTime stopTime = stopTimes.get(position);

        if (v == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            v = inflater.inflate(R.layout.schedule_item, null);
        }

        TextView routeNameTxtView = (TextView) v.findViewById(R.id.schedule_text_view);
        routeNameTxtView.setText(stopTime.toString());

        ImageView duration = (ImageView) v.findViewById(R.id.fast);
        if (stopTime.isBestTripDuration()) {
            duration.setBackgroundColor(Color.parseColor("#10c390"));
        } else if(!stopTime.isBestTripDuration() && stopTime.getId()!= secondFastest){
            duration.setBackgroundColor(Color.parseColor("#eeeef4"));
        }

        ImageView empty = (ImageView) v.findViewById(R.id.empty);
        if (stopTime.isBestNumPassengers()) {
            empty.setBackgroundColor(Color.parseColor("#f68d91"));
        } else {
            empty.setBackgroundColor(Color.parseColor("#eeeef4"));
        }





        /*ImageView duration = (ImageView) v.findViewById(R.id.fast_bus_icon);
        if (stopTime.isBestTripDuration()) {
            duration.setVisibility(View.VISIBLE);
        } else {
            duration.setVisibility(View.GONE);
        }

        ImageView empty = (ImageView) v.findViewById(R.id.empty_bus_icon);
        if (stopTime.isBestNumPassengers()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }*/

        return v;
    }

    private void sort(){
        double bestNumberOfPassengers = Integer.MAX_VALUE;
        double bestTripDuration = Integer.MAX_VALUE;

        int idBestNumberOfPassengers = 0;
        int idBestTripDuration = 0;

        for (StopTime stoptime :stopTimes) {
            if (!stoptime.isBestNumPassengers()){
                if (stoptime.getNumberOfPassengers() < bestNumberOfPassengers){
                    bestNumberOfPassengers = stoptime.getNumberOfPassengers();
                    idBestNumberOfPassengers = stoptime.getId();
                }
            }

            if (!stoptime.isBestTripDuration()){
                if (stoptime.getTripDuration() < bestTripDuration){
                    bestTripDuration = stoptime.getTripDuration();
                    idBestTripDuration = stoptime.getId();
                }
            }
        }
        secondFastest = idBestTripDuration;
        secondEmptiest = idBestNumberOfPassengers;

    }


}
