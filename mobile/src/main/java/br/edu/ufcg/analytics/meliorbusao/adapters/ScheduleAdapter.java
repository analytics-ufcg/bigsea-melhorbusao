package br.edu.ufcg.analytics.meliorbusao.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;


public class ScheduleAdapter extends ArrayAdapter<StopTime> {

    private List<StopTime> stopTimes;
    private Activity activity;

    private List<StopTime> stopTimesByTripDuration;
    private List<StopTime> stopTimesByNumberOfPassengers;
    private int[] icons = new int[]{R.drawable.ic_best_trophy_1,
            R.drawable.ic_best_trophy_2, R.drawable.ic_best_trophy_3};

    public ScheduleAdapter(Activity activity, int resource, List<StopTime> stopTimes) {
        super(activity, resource, stopTimes);
        this.stopTimes = stopTimes;
        this.activity = activity;
        stopTimesByTripDuration = stopTimes;
        stopTimesByNumberOfPassengers = stopTimes;
    }

    /**
     * Retorna o item (parada a ser escolhida) do dropdown
     */
    @Override
    public StopTime getItem(int position) {
        return stopTimes.get(position);
    }

    /**
     * Monta a interface de Pegar Busão / Proximos Horários
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

        ImageView tripDurationPlacing = (ImageView) v.findViewById(R.id.durationPlacingView);
        ImageView numberOfPassengersPlacing = (ImageView) v.findViewById(R.id.crowdPlacingView);

        int durantionPlacing = getPlacingForTripDuration(position);
        int passengersPlacing = getPlacingForPassengersNumber(position);

        tripDurationPlacing.setImageDrawable(getRatingIcon(durantionPlacing));
        tripDurationPlacing.setColorFilter(Color.parseColor(getRatingColorForTripDuration(durantionPlacing)));

        numberOfPassengersPlacing.setImageDrawable(getRatingIcon(passengersPlacing));
        numberOfPassengersPlacing.setColorFilter(Color.parseColor(getRatingColorForPassengersNumber(passengersPlacing)));

        return v;
    }

    private int getPlacingForTripDuration(int position) {
        TripDurationComparator durationComparator = new TripDurationComparator();
        Collections.sort(stopTimesByTripDuration, durationComparator);
        StopTime item = getItem(position);
        int itemIndex = stopTimesByTripDuration.indexOf(item);
        return itemIndex;
    }

    private int getPlacingForPassengersNumber(int position) {
        PassengersComparator passengersComparator = new PassengersComparator();
        Collections.sort(stopTimesByNumberOfPassengers, passengersComparator);
        StopTime item = getItem(position);
        int itemIndex = stopTimesByNumberOfPassengers.indexOf(item);
        return itemIndex;
    }
    
    private String getRatingColorForTripDuration(int placing) {
        String color = "10c390";
        String oppacity = Integer.toHexString(Math.max(55, 255 - placing*100));
        return "#" + oppacity + color;
    }

    private String getRatingColorForPassengersNumber(int placing) {
        String color = "f68d91";
        String oppacity = Integer.toHexString(Math.max(55, 255 - placing*100));
        return "#" + oppacity + color;
    }

    private Drawable getRatingIcon(int placing) {
        if (placing > 3) {
            placing = 3;
        }
        return activity.getResources().getDrawable(icons[placing]);
    }

    class TripDurationComparator implements Comparator<StopTime> {
        @Override
        public int compare(StopTime stopTime, StopTime other) {
            return (int) (stopTime.getTripDuration() - other.getTripDuration());
        }
    }

    class PassengersComparator implements Comparator<StopTime> {
        @Override
        public int compare(StopTime stopTime, StopTime other) {
            return (int) (stopTime.getNumberOfPassengers() - other.getNumberOfPassengers());
        }
    }
}
