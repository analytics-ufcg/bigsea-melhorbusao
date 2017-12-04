package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Leg;
import br.edu.ufcg.analytics.meliorbusao.utils.StringUtils;

public class ItineraryMapFragment extends Fragment {

    public static final String TAG = "ItineraryMapFragment";
    public static final String ITINERARY = "itinerary";
    public static final int DECODER_PRECISION = 10;
    private MapFragment mMapFragment;
    private Itinerary mItinerary;

    private TextView busCodesTextView;
    private TextView durationTextView;
    private TextView stEndTimeTextView;
    private TextView stBusStopTextView;

    public ItineraryMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ItineraryMapFragment.
     */
    public static ItineraryMapFragment newInstance() {
        ItineraryMapFragment fragment = new ItineraryMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapFragment = new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_itinerary_map, container, false);
        getChildFragmentManager().beginTransaction().replace(R.id.itineraries_map_container, mMapFragment).commit();

        busCodesTextView = (TextView) mainView.findViewById(R.id.itinerary_bus_codes);
        durationTextView = (TextView) mainView.findViewById(R.id.itinerary_duration);
        stEndTimeTextView = (TextView) mainView.findViewById(R.id.itinerary_list_item_st_end_time);
        stBusStopTextView = (TextView) mainView.findViewById(R.id.itinerary_list_item_start_bus_stop);

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            mItinerary = getArguments().getParcelable(ITINERARY);
            putInformationOnItineraryCard();
            drawItinerary();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void drawItinerary() {
        Polyline polyline = null;
        List<List<GeoPoint>> polyLines = new ArrayList<>();

        List<String> encodedPolylinePoints = new ArrayList<>();

        for (Leg l : mItinerary.getLegs()) {
            encodedPolylinePoints.addAll(l.getEncodedPolylinePoints());
        }


        for (String encodedPoints : encodedPolylinePoints) {
            polyLines.add(PolylineEncoder.decode(encodedPoints, DECODER_PRECISION, false));
        }

        for (int i = 0; i < polyLines.size(); i++) {
            polyline = new Polyline(getContext());
            polyline.setPoints(polyLines.get(i));
            polyline.setColor(Color.RED);
            polyline.setWidth(5);
            mMapFragment.drawRoute(polyline);
        }

        //Log.d(TAG, String.copyValueOf(polyline));
    }

    private void putInformationOnItineraryCard() {
        try {
            List<String> busRoutes = new ArrayList<>();
            for (Leg l : mItinerary.getLegs()) {
                busRoutes.add(l.getBusRoute());
            }

            busCodesTextView.setText(StringUtils.getStringListConcat(busRoutes));

            int durationInMins = mItinerary.getDurationInSecs() / 60;
            durationTextView.setText(String.valueOf(durationInMins) + " min");

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            stEndTimeTextView.setText(sdf.format(mItinerary.getDepartureTime()) + " - " +
                    sdf.format(mItinerary.getArrivalTime()));

            stBusStopTextView.setText(mItinerary.getDepartureBusStop());


        } catch (Exception e) {
            Log.e("ItinerariesListAdapter", e.getMessage());
        }
    }

}
