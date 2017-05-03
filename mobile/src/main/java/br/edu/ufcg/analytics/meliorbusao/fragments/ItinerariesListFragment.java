package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.SchedulesProbableParser;
import br.edu.ufcg.analytics.meliorbusao.adapters.ItinerariesAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.RoutesAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.ScheduleAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.StopsAdapter;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;


public class ItinerariesListFragment extends Fragment {

    private static ItinerariesListFragment instance;

    private View mView;
    private ListView mItineraryListView;

    private List<Itinerary> itineraries;

    public static final String TAG = "ITINERARIES_LIST_FRAGMENT";
    private FragmentTitleChangeListener mCallback;
    private ItinerariesAdapter mAdapter;

    public static ItinerariesListFragment getInstance() {
        if (instance == null) {
            ItinerariesListFragment fragment = new ItinerariesListFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            instance = fragment;
        }
        return instance;
    }

    public ItinerariesListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_itineraries_list, container, false);

        ////////ITINERARIES/////////
        mAdapter = new ItinerariesAdapter(getActivity(),itineraries);
        mItineraryListView = (ListView) mView.findViewById(R.id.itineraries_list);
        mItineraryListView.setAdapter(mAdapter);

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (FragmentTitleChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallback.onTitleChange(getResources().getString(R.string.stop_time_title));
        //updateListView();
    }

    public void setItinerariesList(List<Itinerary> itineraries){
        this.itineraries = itineraries;
    }
}
