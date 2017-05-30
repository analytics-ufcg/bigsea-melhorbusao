package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.adapters.ItinerariesAdapter;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;


public class ItinerariesListFragment extends Fragment {

    public static final String TAG = "ITINERARIES_LIST_FRAGMENT";
    private static ItinerariesListFragment instance;
    private View mView;
    private RecyclerView itineraryRecyclerView;
    private List<Itinerary> itineraries;
    private FragmentTitleChangeListener mCallback;
    private ItinerariesAdapter mAdapter;

    public ItinerariesListFragment() {
    }

    public static ItinerariesListFragment getInstance() {
        if (instance == null) {
            ItinerariesListFragment fragment = new ItinerariesListFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            instance = fragment;
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_itineraries_list, container, false);

        ////////ITINERARIES/////////
        mAdapter = new ItinerariesAdapter(itineraries);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        itineraryRecyclerView = (RecyclerView) mView.findViewById(R.id.itineraries_list);
        itineraryRecyclerView.setLayoutManager(layoutManager);
        itineraryRecyclerView.setAdapter(mAdapter);

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
        mCallback.onTitleChange(getResources().getString(R.string.itineraries_list_title));
        //updateListView();
    }

    public void setItinerariesList(List<Itinerary> itineraries) {
        this.itineraries = itineraries;
    }
}
