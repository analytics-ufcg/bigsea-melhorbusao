package br.edu.ufcg.analytics.meliorbusao.adapters;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;
import br.edu.ufcg.analytics.meliorbusao.utils.StringUtils;

public class ItinerariesAdapter extends RecyclerView.Adapter<ItinerariesAdapter.ViewHolder> {

    private List<Itinerary> items;
    private OnItineraryClickListener mOnItineraryClickListener;

    public ItinerariesAdapter(List<Itinerary> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_directions_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Itinerary currItinerary = items.get(position);
        final int pos = position;
        holder.bind(currItinerary);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Itinerary itinerary = items.get(pos);
                if (mOnItineraryClickListener != null) {
                    mOnItineraryClickListener.onClick(itinerary);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItineraryClickListener(OnItineraryClickListener mOnClickListener) {
        this.mOnItineraryClickListener = mOnClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView busCodesTextView;
        private TextView durationTextView;
        private TextView stEndTimeTextView;
        private TextView stBusStopTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            busCodesTextView = (TextView) itemView.findViewById(R.id.itinerary_bus_codes);
            durationTextView = (TextView) itemView.findViewById(R.id.itinerary_duration);
            stEndTimeTextView = (TextView) itemView.findViewById(R.id.itinerary_list_item_st_end_time);
            stBusStopTextView = (TextView) itemView.findViewById(R.id.itinerary_list_item_start_bus_stop);
        }

        public void bind(Itinerary itinerary) {
            try {
                busCodesTextView.setText(StringUtils.getStringListConcat(itinerary.getBusRoutes()));

                int durationInMins = itinerary.getDurationInSecs()/60;
                durationTextView.setText(String.valueOf(durationInMins) + " min");

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
               /* stEndTimeTextView.setText(sdf.format(itinerary.getDepartureTime()) + " - " +
                        sdf.format(itinerary.getArrivalTime()));
*/

                stEndTimeTextView.setText(sdf.format(itinerary.getDepartureTime()) + " - " +
                        sdf.format(itinerary.getArrivalTime()));

                stBusStopTextView.setText(itinerary.getDepartureBusStop());
            } catch (Exception e) {
                Log.e("ItinerariesListAdapter", e.getMessage());
            }
        }
    }

    public interface OnItineraryClickListener {
        void onClick(Itinerary itinerary);
    }
}
